package com.zhoukai.librarysystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhoukai.librarysystem.entity.ReaderInfo;

/** 读者业务接口：提供用户查询、新增、修改和删除功能。 */
public interface ReaderService {
    /** 按账号、姓名或电话等关键词分页查询读者。 */
    Page<ReaderInfo> page(int current, int size, String keyword);

    /** 根据主键查询读者详情。 */
    ReaderInfo getById(Long id);

    /** 新增读者并校验账号是否重复。 */
    void save(ReaderInfo reader);

    /** 修改读者资料或启用状态。 */
    void update(ReaderInfo reader);

    /** 删除读者；实现类会检查是否存在关联借阅记录。 */
    void delete(Long id);
}
