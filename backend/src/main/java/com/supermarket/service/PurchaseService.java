package com.supermarket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.entity.Purchase;

/**
 * 进货服务接口
 */
public interface PurchaseService extends IService<Purchase> {
    
    /**
     * 分页查询进货记录（包含操作员信息）
     */
    Page<Purchase> getPurchasesWithOperator(Page<Purchase> page, String purchaseNumber, String supplierName);
}