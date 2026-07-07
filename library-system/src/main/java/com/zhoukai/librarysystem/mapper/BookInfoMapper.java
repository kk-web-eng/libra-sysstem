package com.zhoukai.librarysystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhoukai.librarysystem.entity.BookInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookInfoMapper extends BaseMapper<BookInfo> {
}
