package com.kassen.hardlink.Service.Impl;

import com.kassen.hardlink.Mapper.SyncMapper;
import com.kassen.hardlink.POJO.SyncOperation;
import com.kassen.hardlink.Service.HardlinkService;
import com.kassen.hardlink.Service.SyncService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyncServiceImpl implements SyncService {

    private final SyncMapper syncMapper;
    private final HardlinkService hardlinkService;

    public SyncServiceImpl(SyncMapper syncMapper, HardlinkService hardlinkService) {
        this.syncMapper = syncMapper;
        this.hardlinkService = hardlinkService;
    }

    @Override
    public Integer addSync(SyncOperation syncOperation) {
        int rowsAffected = syncMapper.addSyncOp(syncOperation);
        if (rowsAffected > 0) {
            SyncOperation createdOperation = syncMapper.selectById(syncOperation.getId());
            hardlinkService.processSyncOp(syncMapper.selectById(createdOperation.getId()));
            return rowsAffected;
        } else {
            return null;
        }
    }

    @Override
    public Integer deleteSync(Integer id) {
        return syncMapper.deleteById(id);
    }

    @Override
    public List<SyncOperation> getSyncList() {
        return syncMapper.getSyncList();
    }

    @Override
    public SyncOperation selectById(Integer id) {
        return syncMapper.selectById(id);
    }

}
