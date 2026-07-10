package com.zhoukai.librarysystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("reader_info")
/**
 * 读者实体：对应 reader_info 表，也就是借书人账号。
 *
 * 关键词：账号、密码、姓名、电话、启用状态。
 */
public class ReaderInfo {
    /** 读者主键，数据库自动递增。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 读者登录账号。当前注册规则要求长度至少为 6 位。 */
    private String readerNo;

    /** 登录密码。当前项目直接保存字符串，正式系统应改为加密后的密码。 */
    private String password;

    /** 读者姓名。注册时可以暂时使用账号，之后由管理员补充。 */
    private String name;

    /** 性别，可选信息。 */
    private String gender;

    /** 联系电话。后台借阅登记使用电话查找读者。 */
    private String phone;

    /** 班级或部门，可选信息。 */
    private String className;

    /** 状态：1=正常，0=停用。停用读者不能借书。 */
    private Integer status;

    /** 账号创建时间。 */
    private LocalDateTime createTime;
}
