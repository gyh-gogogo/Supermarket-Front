package com.supermarket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.entity.SaleItem;
import com.supermarket.mapper.SaleItemMapper;
import com.supermarket.service.SaleItemService;
import org.springframework.stereotype.Service;

@Service
public class SaleItemServiceImpl extends ServiceImpl<SaleItemMapper, SaleItem> implements SaleItemService {
    // 基础CRUD方法由ServiceImpl提供
    // 可以在这里添加自定义业务方法
}
