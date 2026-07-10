package com.zhoukai.librarysystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.entity.BookInfo;
import com.zhoukai.librarysystem.entity.BorrowRecord;
import com.zhoukai.librarysystem.entity.ReaderInfo;
import com.zhoukai.librarysystem.mapper.BookInfoMapper;
import com.zhoukai.librarysystem.mapper.BorrowRecordMapper;
import com.zhoukai.librarysystem.mapper.ReaderInfoMapper;
import com.zhoukai.librarysystem.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
/**
 * 图书流转的核心业务类。
 * 借书、归还和续借都会在这里校验规则，并同步修改借阅记录与可借库存。
 */
public class BorrowServiceImpl implements BorrowService {

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;
    @Autowired
    private BookInfoMapper bookInfoMapper;
    @Autowired
    private ReaderInfoMapper readerInfoMapper;

    @Override
    @Transactional
    public void borrow(Long bookId, Long readerId, Long operatorId) {
    // 使用默认规则借书，借出日为今天，应还日为 30 天后。
        createBorrow(bookId, readerId, LocalDate.now(), LocalDate.now().plusDays(30), operatorId);
    }

    @Override
    @Transactional
    public void borrow(Map<String, Object> body, Long operatorId) {
        // 管理员端借书登记：先从前端 JSON 中取出字段，并去掉首尾空格。
        String readerName = text(body.get("readerName"));
        String readerPhone = text(body.get("readerPhone"));
        String bookIsbn = text(body.get("bookIsbn"));
        String bookName = text(body.get("bookName"));
        LocalDate borrowDate = parseDate(body.get("borrowDate"), "借出时间");
        LocalDate dueDate = parseDate(body.get("dueDate"), "还书日期");
        // 日期必须先通过统一规则，避免保存不合法的借阅记录。
        validateBorrowRange(borrowDate, dueDate);

        if (readerName.isBlank()) throw new RuntimeException("请输入读者姓名");
        if (!readerPhone.matches("\\d{10}")) throw new RuntimeException("读者电话必须是 10 位数字");
        if (bookIsbn.isBlank()) throw new RuntimeException("请输入书籍编号");
        if (bookName.isBlank()) throw new RuntimeException("请输入书籍名称");

        // 先按编号查书，找不到时再按书名查。
        BookInfo book = findBook(bookIsbn, bookName);
        if (book == null) throw new RuntimeException("未找到对应图书，请检查书籍编号或名称");
        if (book.getAvailableCount() <= 0) throw new RuntimeException("该图书库存不足，无法借出");

        // 管理员不需要先去用户管理新增读者。
        // 这里把 10 位电话当作临时 readerNo：存在就更新，不存在就自动创建。
        ReaderInfo reader = readerInfoMapper.selectOne(new LambdaQueryWrapper<ReaderInfo>()
                .eq(ReaderInfo::getReaderNo, readerPhone)
                .last("LIMIT 1"));
        if (reader == null) {
            // 自动创建的临时读者使用默认密码，之后仍可在用户管理中修改。
            reader = new ReaderInfo();
            reader.setReaderNo(readerPhone);
            reader.setPassword("123456");
            reader.setName(readerName);
            reader.setPhone(readerPhone);
            reader.setStatus(1);
            readerInfoMapper.insert(reader);
        } else {
            // 已存在时同步姓名和电话，并恢复为启用状态。
            reader.setName(readerName);
            reader.setPhone(readerPhone);
            reader.setStatus(1);
            readerInfoMapper.updateById(reader);
        }

        // 最后统一执行“扣库存 + 写借阅记录”。
        createBorrowRecord(book, reader, borrowDate, dueDate, operatorId);
    }

    @Override
    @Transactional
    public void borrowByReader(Long bookId, Long readerId, LocalDate dueDate) {
        // 读者端自助借书；没有传日期时默认 30 天后归还。
        LocalDate borrowDate = LocalDate.now();
        createBorrow(bookId, readerId, borrowDate, dueDate == null ? borrowDate.plusDays(30) : dueDate, null);
    }

    @Override
    @Transactional
    public void returnBook(Long recordId) {
        BorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null) throw new RuntimeException("借阅记录不存在");
        if (record.getStatus() != 0 && record.getStatus() != 2) throw new RuntimeException("该记录不是可归还状态");

        record.setStatus(1);
        record.setReturnDate(LocalDate.now());
        borrowRecordMapper.updateById(record);

        BookInfo book = bookInfoMapper.selectById(record.getBookId());
        if (book != null) {
            book.setAvailableCount(book.getAvailableCount() + 1);
            bookInfoMapper.updateById(book);
        }
    }

    @Override
    @Transactional
    public void renew(Long recordId, Long readerId, LocalDate dueDate) {
        BorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null) throw new RuntimeException("借阅记录不存在");
        if (readerId != null && !record.getReaderId().equals(readerId)) throw new RuntimeException("只能续借自己的图书");
        if (record.getStatus() != 0) throw new RuntimeException("只有借阅中的图书可以续借");
        LocalDate nextDueDate = dueDate == null ? record.getDueDate().plusDays(30) : dueDate;
        validateBorrowRange(record.getBorrowDate(), nextDueDate);
        record.setDueDate(nextDueDate);
        borrowRecordMapper.updateById(record);
    }

    @Override
    public Page<BorrowRecord> page(int current, int size, Long bookId, Long readerId, Integer status, String keyword) {
        refreshOverdueStatus();
        Page<BorrowRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        if (bookId != null) wrapper.eq(BorrowRecord::getBookId, bookId);
        if (readerId != null) wrapper.eq(BorrowRecord::getReaderId, readerId);
        if (status != null) wrapper.eq(BorrowRecord::getStatus, status);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(item -> item.like(BorrowRecord::getReaderName, keyword)
                    .or()
                    .like(BorrowRecord::getReaderPhone, keyword)
                    .or()
                    .like(BorrowRecord::getBookIsbn, keyword)
                    .or()
                    .like(BorrowRecord::getBookName, keyword));
        }
        wrapper.orderByDesc(BorrowRecord::getCreateTime);
        return borrowRecordMapper.selectPage(page, wrapper);
    }

    private void createBorrow(Long bookId, Long readerId, LocalDate borrowDate, LocalDate dueDate, Long operatorId) {
        validateBorrowRange(borrowDate, dueDate);
        BookInfo book = bookInfoMapper.selectById(bookId);
        if (book == null) throw new RuntimeException("图书不存在");
        if (book.getAvailableCount() <= 0) throw new RuntimeException("该图书库存不足，无法借出");

        ReaderInfo reader = readerInfoMapper.selectById(readerId);
        if (reader == null) throw new RuntimeException("读者不存在");
        if (reader.getStatus() == 0) throw new RuntimeException("该读者已被停用，无法借书");
        createBorrowRecord(book, reader, borrowDate, dueDate, operatorId);
    }

    private void createBorrowRecord(BookInfo book, ReaderInfo reader, LocalDate borrowDate, LocalDate dueDate, Long operatorId) {
        book.setAvailableCount(book.getAvailableCount() - 1);
        bookInfoMapper.updateById(book);

        BorrowRecord record = new BorrowRecord();
        record.setBookId(book.getId());
        record.setReaderId(reader.getId());
        record.setReaderName(reader.getName());
        record.setReaderPhone(reader.getPhone());
        record.setBookIsbn(book.getIsbn());
        record.setBookName(book.getBookName());
        record.setBorrowDate(borrowDate);
        record.setDueDate(dueDate);
        record.setStatus(0);
        record.setOperatorId(operatorId);
        borrowRecordMapper.insert(record);
    }

    private BookInfo findBook(String bookIsbn, String bookName) {
        BookInfo book = bookInfoMapper.selectOne(new LambdaQueryWrapper<BookInfo>()
                .eq(BookInfo::getIsbn, bookIsbn)
                .last("LIMIT 1"));
        if (book == null) {
            book = bookInfoMapper.selectOne(new LambdaQueryWrapper<BookInfo>()
                    .eq(BookInfo::getBookName, bookName)
                    .last("LIMIT 1"));
        }
        return book;
    }

    private void validateBorrowRange(LocalDate borrowDate, LocalDate dueDate) {
        if (dueDate.isBefore(borrowDate)) throw new RuntimeException("还书日期不能早于借出日期");
        if (dueDate.isAfter(borrowDate.plusMonths(6))) throw new RuntimeException("每次借书不能超过 6 个月");
    }

    private void refreshOverdueStatus() {
        List<BorrowRecord> records = borrowRecordMapper.selectList(new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getStatus, 0));
        LocalDate today = LocalDate.now();
        for (BorrowRecord record : records) {
            if (record.getDueDate() != null && record.getDueDate().isBefore(today)) {
                record.setStatus(2);
                borrowRecordMapper.updateById(record);
            }
        }
    }

    private String text(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private LocalDate parseDate(Object value, String label) {
        String text = text(value);
        if (text.isBlank()) throw new RuntimeException("请选择" + label);
        return LocalDate.parse(text);
    }
}
