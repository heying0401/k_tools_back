package com.kassen.hardlink.Service;

import com.kassen.hardlink.Mapper.SyncMapper;
import com.kassen.hardlink.POJO.SyncOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HardlinkService {

    @Autowired
    SyncMapper syncMapper;

    public void processSyncOp(SyncOperation syncOperation) {



        syncOperation.setStatus(SyncOperation.SyncStatus.IN_PROGRESS);
        syncMapper.updateById(syncOperation);
    }

}
