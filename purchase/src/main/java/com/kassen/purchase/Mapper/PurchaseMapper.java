package com.kassen.purchase.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kassen.purchase.POJO.PurchaseRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PurchaseMapper extends BaseMapper<PurchaseRequest> {

    @Select("select * from KASSEN.PurchaseRequests")
    List<PurchaseRequest> loadRequests ();
}
