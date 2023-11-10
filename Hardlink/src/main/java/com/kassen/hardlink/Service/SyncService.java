package com.kassen.hardlink.Service;

import com.kassen.hardlink.POJO.SyncOperation;

import java.util.List;

public interface SyncService {

    SyncOperation addSync(SyncOperation syncOperation);
    Integer deleteSync(Integer id);
    List<SyncOperation> getSyncList();
    SyncOperation selectById(Integer id);

}
