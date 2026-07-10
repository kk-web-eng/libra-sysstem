package com.zhoukai.librarysystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhoukai.librarysystem.entity.ReaderInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/**
 * 读者数据访问接口。
 * Service 层通过它查询账号、电话和读者状态，并完成用户信息维护。
 */
public interface ReaderInfoMapper extends BaseMapper<ReaderInfo> {
}
