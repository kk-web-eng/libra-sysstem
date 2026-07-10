package com.zhoukai.librarysystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.entity.SysUser;
import com.zhoukai.librarysystem.mapper.SysUserMapper;
import com.zhoukai.librarysystem.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
/** 实现后台管理员登录和账号维护。 */
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public SysUser login(String username, String password) {
        // 登录条件：账号、密码正确，并且账号处于启用状态。
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username)
               .eq(SysUser::getPassword, password)
               .eq(SysUser::getStatus, 1);
        SysUser user = sysUserMapper.selectOne(wrapper);
        // admin 是系统预置账号。旧数据库如果没有正确角色，会在登录时自动修复。
        if (user != null && "admin".equals(user.getUsername()) && !"SUPER_ADMIN".equals(user.getRole())) {
            user.setRole("SUPER_ADMIN");
            sysUserMapper.updateById(user);
        }
        return user;
    }

    @Override
    public Page<SysUser> page(int current, int size, String keyword) {
        // 支持按登录账号或真实姓名搜索。
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getRealName, keyword);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        return sysUserMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public void save(SysUser user) {
        // 新账号默认启用，并把非法或空角色统一为普通管理员。
        normalizeRole(user);
        if (user.getStatus() == null) user.setStatus(1);
        sysUserMapper.insert(user);
    }

    @Override
    public void update(SysUser user) {
        normalizeRole(user);
        sysUserMapper.updateById(user);
    }

    @Override
    public void delete(Long id) {
        sysUserMapper.deleteById(id);
    }

    private void normalizeRole(SysUser user) {
        // 系统只认识两种角色：SUPER_ADMIN 和 ADMIN。
        if (!"SUPER_ADMIN".equals(user.getRole())) {
            user.setRole("ADMIN");
        }
    }
}
