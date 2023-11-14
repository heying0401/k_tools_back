package com.kassen.hardlink.Service.Impl;

import com.kassen.hardlink.Mapper.SyncMapper;
import com.kassen.hardlink.POJO.SyncOperation;
import com.kassen.hardlink.Service.HardlinkService;
import com.kassen.hardlink.Service.SyncService;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class SyncServiceImpl implements SyncService {

    private final SyncMapper syncMapper;
    private final HardlinkService hardlinkService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();



    public SyncServiceImpl(SyncMapper syncMapper, HardlinkService hardlinkService) {
        this.syncMapper = syncMapper;
        this.hardlinkService = hardlinkService;
    }

    @Override
    public Integer addSync(SyncOperation syncOperation) {
        int rowsAffected = syncMapper.addSyncOp(syncOperation);
        if (rowsAffected > 0) {
            SyncOperation createdOperation = syncMapper.selectById(syncOperation.getId());
            executorService.submit(() -> hardlinkService.processSyncOp(createdOperation));
            return rowsAffected;
        } else {
            return null;
        }
    }

    @Override
    public Integer deleteSync(Integer id) {
        return hardlinkService.completeSyncOperation(id);
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
