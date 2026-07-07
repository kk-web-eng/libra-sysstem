package com.zhoukai.librarysystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("book_info")
public class BookInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String isbn;
    private String bookName;
    private String author;
    private String publisher;
    private String category;
    private String description;
    private Integer totalCount;
    private Integer availableCount;
    private String location;
    private LocalDateTime createTime;
}
