package com.zhoukai.librarysystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.entity.BookInfo;
import java.util.List;

public interface BookService {
    Page<BookInfo> page(int current, int size, String keyword);
    Page<BookInfo> page(int current, int size, String keyword, String category);
    List<String> categories();
    BookInfo getById(Long id);
    void save(BookInfo book);
    void update(BookInfo book);
    void delete(Long id);
}
