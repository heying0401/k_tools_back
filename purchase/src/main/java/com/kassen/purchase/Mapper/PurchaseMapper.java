package com.kassen.purchase.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kassen.purchase.POJO.PurchaseRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PurchaseMapper extends BaseMapper<PurchaseRequest> {
}
