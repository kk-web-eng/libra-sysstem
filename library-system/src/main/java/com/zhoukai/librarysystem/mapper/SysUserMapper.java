package com.zhoukai.librarysystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhoukai.librarysystem.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/**
 * 管理员数据访问接口。
 * 用于管理员登录、账号分页查询以及系统管理员维护普通管理员。
 */
public interface SysUserMapper extends BaseMapper<SysUser> {
}
