package com.zhoukai.librarysystem.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhoukai.librarysystem.common.Result;
import com.zhoukai.librarysystem.entity.BorrowRecord;
import com.zhoukai.librarysystem.mapper.BookInfoMapper;
import com.zhoukai.librarysystem.mapper.BorrowRecordMapper;
import com.zhoukai.librarysystem.mapper.ReaderInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
/** 为后台首页汇总运营数据。 */
public class DashboardController {

    @Autowired
    private BookInfoMapper bookInfoMapper;
    @Autowired
    private ReaderInfoMapper readerInfoMapper;
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @GetMapping
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        // 图书和用户使用整表数量；借阅数据根据 status 分组统计。
        data.put("bookCount", bookInfoMapper.selectCount(null));
        data.put("readerCount", readerInfoMapper.selectCount(null));
        // status：0 借阅中，1 已归还，2 逾期。
        data.put("borrowingCount", borrowRecordMapper.selectCount(
                new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getStatus, 0)));
        data.put("overdueCount", borrowRecordMapper.selectCount(
                new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getStatus, 2)));
        return Result.ok(data);
    }
}
