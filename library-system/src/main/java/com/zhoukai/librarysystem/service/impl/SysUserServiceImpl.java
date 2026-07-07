package com.zhoukai.librarysystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.entity.SysUser;
import com.zhoukai.librarysystem.mapper.SysUserMapper;
import com.zhoukai.librarysystem.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public SysUser login(String username, String password) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username)
               .eq(SysUser::getPassword, password)
               .eq(SysUser::getStatus, 1);
        return sysUserMapper.selectOne(wrapper);
    }

    @Override
    public Page<SysUser> page(int current, int size, String keyword) {
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
        if (user.getRole() == null || user.getRole().isBlank()) user.setRole("ADMIN");
        if (user.getStatus() == null) user.setStatus(1);
        sysUserMapper.insert(user);
    }

    @Override
    public void update(SysUser user) {
        sysUserMapper.updateById(user);
    }

    @Override
    public void delete(Long id) {
        sysUserMapper.deleteById(id);
    }
}
