package com.zhoukai.librarysystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.entity.ReaderInfo;

public interface ReaderService {
    Page<ReaderInfo> page(int current, int size, String keyword);
    ReaderInfo getById(Long id);
    void save(ReaderInfo reader);
    void update(ReaderInfo reader);
    void delete(Long id);
}
