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
/** 实现图书查询、分类、新增、库存调整和删除规则。 */
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
        // Page 保存当前页、每页数量以及查询后的总条数和记录列表。
        Page<BookInfo> page = new Page<>(current, size);
        LambdaQueryWrapper<BookInfo> wrapper = new LambdaQueryWrapper<>();
        // 关键词同时匹配书名和 ISBN。
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(item -> item.like(BookInfo::getBookName, keyword)
                    .or()
                    .like(BookInfo::getIsbn, keyword));
        }
        // 分类不为空时追加精确匹配条件。
        if (category != null && !category.isBlank()) {
            wrapper.eq(BookInfo::getCategory, category);
        }
        wrapper.orderByDesc(BookInfo::getCreateTime);
        return bookInfoMapper.selectPage(page, wrapper);
    }

    @Override
    public List<String> categories() {
        // DISTINCT 只返回不重复的分类，并排除 null 和空字符串。
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
        // 新书还没有借出记录，所以初始可借数量等于馆藏总数量。
        book.setAvailableCount(book.getTotalCount());
        bookInfoMapper.insert(book);
    }

    @Override
    public void update(BookInfo book) {
        // 修改馆藏总数时不能直接重置可借数量，否则会忘记已经借出的数量。
        // 例如原来总数 5、可借 3，说明借出 2 本；总数改成 7 后，可借应变成 5。
        BookInfo old = bookInfoMapper.selectById(book.getId());
        int diff = book.getTotalCount() - old.getTotalCount();
        book.setAvailableCount(old.getAvailableCount() + diff);
        bookInfoMapper.updateById(book);
    }

    @Override
    public void delete(Long id) {
        // 删除前检查是否还有 status=0 的记录，避免借阅记录指向不存在的图书。
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getBookId, id)
               .eq(BorrowRecord::getStatus, 0);
        if (borrowRecordMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("该图书存在未归还的借阅记录，无法删除");
        }
        bookInfoMapper.deleteById(id);
    }
}
