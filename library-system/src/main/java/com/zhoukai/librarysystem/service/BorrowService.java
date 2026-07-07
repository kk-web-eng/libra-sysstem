package com.zhoukai.librarysystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.entity.BorrowRecord;
import java.time.LocalDate;
import java.util.Map;

public interface BorrowService {
    void borrow(Long bookId, Long readerId, Long operatorId);
    void borrow(Map<String, Object> body, Long operatorId);
    void borrowByReader(Long bookId, Long readerId, LocalDate dueDate);
    void returnBook(Long recordId);
    void renew(Long recordId, Long readerId, LocalDate dueDate);
    Page<BorrowRecord> page(int current, int size, Long bookId, Long readerId, Integer status, String keyword);
}
