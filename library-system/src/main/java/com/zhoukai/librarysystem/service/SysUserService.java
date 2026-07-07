package com.zhoukai.librarysystem.service;

import com.zhoukai.librarysystem.entity.SysUser;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface SysUserService {
    SysUser login(String username, String password);
    Page<SysUser> page(int current, int size, String keyword);
    void save(SysUser user);
    void update(SysUser user);
    void delete(Long id);
}
