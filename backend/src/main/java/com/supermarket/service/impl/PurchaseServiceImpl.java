package com.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.entity.Purchase;
import com.supermarket.mapper.PurchaseMapper;
import com.supermarket.service.PurchaseService;
import org.springframework.stereotype.Service;

/**
 * 进货服务实现类
 */
@Service
public class PurchaseServiceImpl extends ServiceImpl<PurchaseMapper, Purchase> implements PurchaseService {
    
    @Override
    public Page<Purchase> getPurchasesWithOperator(Page<Purchase> page, String purchaseNumber, String supplierName) {
        QueryWrapper<Purchase> queryWrapper = new QueryWrapper<>();
        
        if (purchaseNumber != null && !purchaseNumber.trim().isEmpty()) {
            queryWrapper.like("purchase_number", purchaseNumber);
        }
        
        if (supplierName != null && !supplierName.trim().isEmpty()) {
            queryWrapper.like("supplier_name", supplierName);
        }
        
        queryWrapper.orderByDesc("purchase_date");
        return this.page(page, queryWrapper);
    }
}
