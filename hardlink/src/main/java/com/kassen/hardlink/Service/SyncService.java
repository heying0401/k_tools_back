package com.kassen.hardlink.Service;

import com.kassen.hardlink.POJO.SyncOperation;

import java.util.List;

public interface SyncService {

    Integer addSync(SyncOperation syncOperation);
    Integer deleteSync(Integer id);
    SyncOperation selectById(Integer id);
    int updateOneStatus(Integer id, String status);
    int updateAllStatus(SyncOperation.SyncStatus status);
    List<SyncOperation> findAllByStatus(SyncOperation.SyncStatus status);
    List<SyncOperation> getAll();
}
