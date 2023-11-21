package com.kassen.hardlink.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kassen.hardlink.POJO.SyncOperation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SyncMapper extends BaseMapper<SyncOperation> {

    @Select("SELECT * FROM hardlink.sync_operations")
    List<SyncOperation> getAll();

    @Insert("INSERT INTO hardlink.sync_operations(root, target, status, duration_seconds) values (#{root}, #{target}, #{status}, #{durationSeconds})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int addSyncOp(SyncOperation syncOperation);
}
