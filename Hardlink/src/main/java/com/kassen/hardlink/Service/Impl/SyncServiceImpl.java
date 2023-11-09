package com.kassen.hardlink.Service.Impl;

import com.kassen.hardlink.Mapper.SyncMapper;
import com.kassen.hardlink.POJO.SyncOperation;
import com.kassen.hardlink.Service.SyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyncServiceImpl implements SyncService {

    private final SyncMapper syncMapper;

    public SyncServiceImpl(SyncMapper syncMapper) {
        this.syncMapper = syncMapper;
    }

    @Override
    public Integer addSync(SyncOperation syncOperation) {
        return syncMapper.insert(syncOperation);
    }

    @Override
    public Integer deleteSync(Integer id) {
        return syncMapper.deleteById(id);
    }

    @Override
    public List<SyncOperation> getSyncList() {
        return syncMapper.getSyncList();
    }
}
