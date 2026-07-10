package com.zhoukai.librarysystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.entity.BorrowRecord;
import java.time.LocalDate;
import java.util.Map;

/**
 * 借阅业务接口。
 *
 * 关键词：借书、归还、续借、逾期、库存同步。
 * 借阅操作会同时影响借阅记录和图书可借数量，所以实现类使用事务保证一致性。
 */
public interface BorrowService {
    /** 按图书 ID 和读者 ID 借书，默认借出当天并设置默认应还日期。 */
    void borrow(Long bookId, Long readerId, Long operatorId);

    /**
     * 管理员登记借书。
     * Map 中包含读者姓名、电话、书籍编号、书名、借出日期和应还日期。
     * 实现类会校验信息、查找或创建读者，再生成借阅记录并扣减库存。
     */
    void borrow(Map<String, Object> body, Long operatorId);

    /**
     * 读者端自助借书。
     * readerId 来自当前登录 Session，dueDate 是读者在日期选择器中选择的应还日期。
     */
    void borrowByReader(Long bookId, Long readerId, LocalDate dueDate);

    /** 归还图书：更新记录状态和实际归还日期，并恢复可借库存。 */
    void returnBook(Long recordId);

    /**
     * 续借：只允许借阅中的记录操作，并更新新的应还日期。
     * readerId 不为空时还会检查记录是否属于当前读者，防止续借他人的图书。
     */
    void renew(Long recordId, Long readerId, LocalDate dueDate);

    /** 分页查询借阅记录，可按图书、读者、状态和关键词筛选。 */
    Page<BorrowRecord> page(int current, int size, Long bookId, Long readerId, Integer status, String keyword);
}
