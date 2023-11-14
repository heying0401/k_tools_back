package com.kassen.hardlink.Service;

import com.kassen.hardlink.Mapper.SyncMapper;
import com.kassen.hardlink.POJO.SyncOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class HardlinkService {

    @Autowired
    SyncMapper syncMapper;

    public void processSyncOp(SyncOperation syncOperation) {



        syncMapper.updateById(syncOperation);

    }

}
