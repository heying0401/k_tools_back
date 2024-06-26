package com.kassen.sync.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kassen.sync.Mapper.SyncMapper;
import com.kassen.sync.POJO.SyncOperation;
import com.kassen.sync.Service.HardlinkService;
import com.kassen.sync.Service.SyncService;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SyncServiceImpl implements SyncService {
    private static final Logger logger = Logger.getLogger(SyncServiceImpl.class.getName());
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
            createdOperation.setStatus(SyncOperation.SyncStatus.IN_PROGRESS);
            syncMapper.updateById(createdOperation);
            executorService.submit(() -> {
                try {
                    hardlinkService.processSyncOp(createdOperation);
                } catch (Exception e) {
                    createdOperation.setStatus(SyncOperation.SyncStatus.FAILED);
                    syncMapper.updateById(createdOperation);
                    logger.log(Level.SEVERE, "Failed to process sync operation: " + createdOperation.getId(), e);
                }
            });
            return rowsAffected;
        } else {
            return null;
        }
    }

    @Override
    public Integer deleteSync(Integer id) {
        hardlinkService.completeSyncOperation(id);
        return syncMapper.deleteById(id);
    }

    @Override
    public List<SyncOperation> findAllByStatus(SyncOperation.SyncStatus status){
        QueryWrapper<SyncOperation> wrapper = new QueryWrapper<>();
        wrapper.eq("status", status);
        return syncMapper.selectList(wrapper);
    }

    @Override
    public List<SyncOperation> getAll() {
        return syncMapper.getAll();
    }

    @Override
    public SyncOperation selectById(Integer id) {
        return syncMapper.selectById(id);
    }

    @Override
    public int updateOneStatus(Integer id, SyncOperation.SyncStatus status) {
        SyncOperation op = syncMapper.selectById(id);
        if (op != null) {
            op.setStatus(status);
            if (status == SyncOperation.SyncStatus.PAUSED){
                hardlinkService.completeSyncOperation(id);
            } else if (status == SyncOperation.SyncStatus.IN_PROGRESS) {
                resumeOperations(Collections.singletonList(op));
            }
            return syncMapper.updateById(op);
        }
        return 0;
    }

    @Override
    @Transactional
    public int updateAllStatus(SyncOperation.SyncStatus status) {
        List<SyncOperation> list = syncMapper.getAll();
        for (SyncOperation syncOperation : list) {
            syncOperation.setStatus(status);
            syncMapper.updateById(syncOperation);
        }
        return list.size();
    }

    @EventListener(ContextRefreshedEvent.class)
    public void resumeOperationsOnStart() {
        List<SyncOperation> operationsToResume = findAllByStatus(SyncOperation.SyncStatus.STOPPED);
        resumeOperations(operationsToResume);
    }

    public void resumeOperations(List<SyncOperation> operationsToResume) {
        System.out.println(operationsToResume);
        for (SyncOperation operation : operationsToResume) {
            try {
                hardlinkService.processSyncOp(operation);
                operation.setStatus(SyncOperation.SyncStatus.IN_PROGRESS);
            } catch (Exception e) {
                operation.setStatus(SyncOperation.SyncStatus.FAILED);
            }
            syncMapper.updateById(operation);
        }
    }
}
