package com.kassen.sync.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kassen.sync.POJO.SyncOperation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SyncMapper extends BaseMapper<SyncOperation> {

    @Select("SELECT * FROM KASSEN.sync_operations")
    List<SyncOperation> getAll();

    @Insert("INSERT INTO KASSEN.sync_operations(root, target, status, duration_seconds) values (#{root}, #{target}, #{status}, #{durationSeconds})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int addSyncOp(SyncOperation syncOperation);
}
