package com.kassen.hardlink.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kassen.hardlink.POJO.SyncOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SyncMapper extends BaseMapper<SyncOperation> {

    @Select("SELECT * FROM hardlink.sync_operations WHERE status = 'IN_PROGRESS'")
    List<SyncOperation> getSyncList();


}
