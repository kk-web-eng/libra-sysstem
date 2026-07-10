package com.zhoukai.librarysystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhoukai.librarysystem.entity.BookInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/**
 * 图书数据访问接口。
 *
 * 关键词：Mapper、数据库操作、BaseMapper。
 * 继承 BaseMapper 后，MyBatis Plus 会自动提供 selectById、insert、
 * updateById、deleteById、selectPage 等常用方法，因此这里暂时不需要手写 SQL。
 */
public interface BookInfoMapper extends BaseMapper<BookInfo> {
}
