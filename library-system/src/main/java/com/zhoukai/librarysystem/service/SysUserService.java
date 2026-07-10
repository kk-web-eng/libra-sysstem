package com.zhoukai.librarysystem.service;

import com.zhoukai.librarysystem.entity.SysUser;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/** 后台管理员业务接口：处理登录和管理员账号维护。 */
public interface SysUserService {
    /** 根据账号和密码登录，只返回状态正常的管理员。 */
    SysUser login(String username, String password);

    /** 分页查询管理员账号。Controller 会先检查当前用户是否为系统管理员。 */
    Page<SysUser> page(int current, int size, String keyword);

    /** 新增普通管理员。角色默认为 ADMIN。 */
    void save(SysUser user);

    /** 修改管理员姓名、密码或启用状态。 */
    void update(SysUser user);

    /** 删除管理员账号，并保护内置系统管理员不被删除。 */
    void delete(Long id);
}
