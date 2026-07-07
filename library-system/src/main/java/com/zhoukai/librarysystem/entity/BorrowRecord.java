package com.zhoukai.librarysystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("borrow_record")
public class BorrowRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookId;
    private Long readerId;
    private String readerName;
    private String readerPhone;
    private String bookIsbn;
    private String bookName;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Integer status;
    private Long operatorId;
    private String remark;
    private LocalDateTime createTime;
}
