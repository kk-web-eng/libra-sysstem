package com.zhoukai.librarysystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("reader_info")
public class ReaderInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String readerNo;
    private String password;
    private String name;
    private String gender;
    private String phone;
    private String className;
    private Integer status;
    private LocalDateTime createTime;
}
