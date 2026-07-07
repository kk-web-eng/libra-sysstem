package com.zhoukai.librarysystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.entity.BorrowRecord;
import com.zhoukai.librarysystem.entity.ReaderInfo;
import com.zhoukai.librarysystem.mapper.BorrowRecordMapper;
import com.zhoukai.librarysystem.mapper.ReaderInfoMapper;
import com.zhoukai.librarysystem.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReaderServiceImpl implements ReaderService {

    @Autowired
    private ReaderInfoMapper readerInfoMapper;
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Override
    public Page<ReaderInfo> page(int current, int size, String keyword) {
        Page<ReaderInfo> page = new Page<>(current, size);
        LambdaQueryWrapper<ReaderInfo> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(ReaderInfo::getName, keyword)
                   .or()
                   .like(ReaderInfo::getReaderNo, keyword);
        }
        wrapper.orderByDesc(ReaderInfo::getCreateTime);
        return readerInfoMapper.selectPage(page, wrapper);
    }

    @Override
    public ReaderInfo getById(Long id) {
        return readerInfoMapper.selectById(id);
    }

    @Override
    public void save(ReaderInfo reader) {
        validate(reader);
        readerInfoMapper.insert(reader);
    }

    @Override
    public void update(ReaderInfo reader) {
        validate(reader);
        readerInfoMapper.updateById(reader);
    }

    @Override
    public void delete(Long id) {
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getReaderId, id)
               .eq(BorrowRecord::getStatus, 0);
        if (borrowRecordMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("该用户存在未归还的借阅记录，无法删除");
        }
        readerInfoMapper.deleteById(id);
    }

    private void validate(ReaderInfo reader) {
        if (reader.getReaderNo() == null || reader.getReaderNo().trim().length() < 6) {
            throw new RuntimeException("账号至少 6 位");
        }
        if (reader.getPassword() == null || reader.getPassword().isBlank()) {
            throw new RuntimeException("请输入密码");
        }
    }
}
