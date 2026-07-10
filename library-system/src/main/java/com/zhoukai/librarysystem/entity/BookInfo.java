package com.zhoukai.librarysystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("book_info")
/**
 * 图书实体：对应数据库中的 book_info 表。
 *
 * 关键词：馆藏、库存、可借数量。
 * @Data 由 Lombok 自动生成 getter、setter、toString 等常用方法。
 * @TableName 告诉 MyBatis Plus 当前 Java 类对应哪张数据库表。
 */
public class BookInfo {
    /** 主键：数据库自动递增，不需要前端手动填写。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 图书编号：可以保存 ISBN，也可以保存图书馆自定义编号。 */
    private String isbn;

    /** 书名。Java 属性 bookName 会自动映射到表字段 book_name。 */
    private String bookName;

    /** 作者。 */
    private String author;

    /** 出版社。 */
    private String publisher;

    /** 分类：用于馆藏页面的分类筛选。 */
    private String category;

    /** 图书简介：帮助读者在借书前了解内容。 */
    private String description;

    /** 馆藏总数：图书馆实际拥有的总册数。 */
    private Integer totalCount;

    /**
     * 可借数量：当前还能借出的册数。
     * 借书成功后减 1，归还成功后加 1，不能小于 0。
     */
    private Integer availableCount;

    /** 书架位置，例如 A-01-01。 */
    private String location;

    /** 创建时间：由数据库记录该图书第一次入库的时间。 */
    private LocalDateTime createTime;
}
