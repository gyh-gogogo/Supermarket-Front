package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.entity.Purchase;
import org.apache.ibatis.annotations.Mapper;

/**
 * 进货Mapper接口
 */
@Mapper
public interface PurchaseMapper extends BaseMapper<Purchase> {
    
}
