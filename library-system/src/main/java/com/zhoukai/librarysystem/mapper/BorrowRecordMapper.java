package com.zhoukai.librarysystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhoukai.librarysystem.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/**
 * 借阅记录数据访问接口。
 * 负责 borrow_record 表的新增、查询、修改和分页操作。
 */
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {
}
