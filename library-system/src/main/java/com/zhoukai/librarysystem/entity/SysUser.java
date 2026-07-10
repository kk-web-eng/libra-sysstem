package com.zhoukai.librarysystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
/**
 * 后台管理员实体：对应 sys_user 表。
 *
 * 关键词：管理员账号、角色、权限、启用状态。
 */
public class SysUser {
    /** 管理员主键，数据库自动递增。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 管理员登录账号，数据库中不能重复。 */
    private String username;

    /** 登录密码。当前项目为明文演示，正式系统应保存密码哈希。 */
    private String password;

    /** 管理员真实姓名，用于页面显示。 */
    private String realName;

    /**
     * 角色：SUPER_ADMIN=系统管理员，ADMIN=普通管理员。
     * 只有系统管理员可以进入“管理员管理”模块。
     */
    private String role;

    /** 状态：1=启用，0=停用。停用账号不能登录后台。 */
    private Integer status;

    /** 管理员账号创建时间。 */
    private LocalDateTime createTime;
}
