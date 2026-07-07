package com.zhoukai.librarysystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.entity.BookInfo;
import com.zhoukai.librarysystem.entity.BorrowRecord;
import com.zhoukai.librarysystem.mapper.BookInfoMapper;
import com.zhoukai.librarysystem.mapper.BorrowRecordMapper;
import com.zhoukai.librarysystem.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookInfoMapper bookInfoMapper;
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Override
    public Page<BookInfo> page(int current, int size, String keyword) {
        return page(current, size, keyword, null);
    }

    @Override
    public Page<BookInfo> page(int current, int size, String keyword, String category) {
        Page<BookInfo> page = new Page<>(current, size);
        LambdaQueryWrapper<BookInfo> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(item -> item.like(BookInfo::getBookName, keyword)
                    .or()
                    .like(BookInfo::getIsbn, keyword));
        }
        if (category != null && !category.isBlank()) {
            wrapper.eq(BookInfo::getCategory, category);
        }
        wrapper.orderByDesc(BookInfo::getCreateTime);
        return bookInfoMapper.selectPage(page, wrapper);
    }

    @Override
    public List<String> categories() {
        QueryWrapper<BookInfo> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT category")
                .isNotNull("category")
                .ne("category", "")
                .orderByAsc("category");
        return bookInfoMapper.selectObjs(wrapper).stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .toList();
    }

    @Override
    public BookInfo getById(Long id) {
        return bookInfoMapper.selectById(id);
    }

    @Override
    public void save(BookInfo book) {
        book.setAvailableCount(book.getTotalCount());
        bookInfoMapper.insert(book);
    }

    @Override
    public void update(BookInfo book) {
        BookInfo old = bookInfoMapper.selectById(book.getId());
        int diff = book.getTotalCount() - old.getTotalCount();
        book.setAvailableCount(old.getAvailableCount() + diff);
        bookInfoMapper.updateById(book);
    }

    @Override
    public void delete(Long id) {
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getBookId, id)
               .eq(BorrowRecord::getStatus, 0);
        if (borrowRecordMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("该图书存在未归还的借阅记录，无法删除");
        }
        bookInfoMapper.deleteById(id);
    }
}
