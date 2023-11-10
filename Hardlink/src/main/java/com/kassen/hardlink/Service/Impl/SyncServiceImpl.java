package com.kassen.hardlink.Service.Impl;

import com.kassen.hardlink.Mapper.SyncMapper;
import com.kassen.hardlink.POJO.SyncOperation;
import com.kassen.hardlink.Service.SyncService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyncServiceImpl implements SyncService {

    private final SyncMapper syncMapper;

    public SyncServiceImpl(SyncMapper syncMapper) {
        this.syncMapper = syncMapper;
    }

    @Override
    public SyncOperation addSync(SyncOperation syncOperation) {
        int rowsAffected = syncMapper.addSyncOp(syncOperation);
        if (rowsAffected > 0) {
            // The id property of syncOperation will now be set to the generated key
            // If you need to fetch the full entity, you can do so like this:
            return syncMapper.selectById(syncOperation.getId());
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
