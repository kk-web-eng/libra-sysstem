package com.zhoukai.librarysystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.entity.BookInfo;
import java.util.List;

/**
 * 图书业务接口。
 * Controller 只负责接收请求，具体的查询条件、库存检查和数据保存由实现类处理。
 */
public interface BookService {
    /** 按关键词分页查询图书。 */
    Page<BookInfo> page(int current, int size, String keyword);

    /** 按关键词和分类分页查询，公共馆藏和读者端会使用这个方法。 */
    Page<BookInfo> page(int current, int size, String keyword, String category);

    /** 查询当前数据库中已有的分类列表。 */
    List<String> categories();

    /** 根据主键查询一本图书。 */
    BookInfo getById(Long id);

    /** 新增图书，并检查编号、库存等数据是否合法。 */
    void save(BookInfo book);

    /** 修改已有图书信息。 */
    void update(BookInfo book);

    /** 删除图书；存在借阅记录时由实现类决定是否允许删除。 */
    void delete(Long id);
}
