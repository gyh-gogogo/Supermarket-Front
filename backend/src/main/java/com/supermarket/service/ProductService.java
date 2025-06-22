package com.supermarket.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.entity.Product;

import java.util.List;
import java.util.Map;

/**
 * 商品服务接口
 */
public interface ProductService extends IService<Product> {
    
    /**
     * 分页查询商品
     */
    IPage<Product> getPage(int current, int size, String productName);
    
    /**
     * 创建商品
     */
    Product create(Product product);
    
    /**
     * 更新商品
     */
    Product update(Product product);
    
    /**
     * 删除商品
     */
    boolean delete(Long productId);
    
    /**
     * 获取所有商品
     */
    List<Product> getAll();
    
    /**
     * 根据ID获取商品
     */
    Product getById(Long productId);
    
    /**
     * 根据条码查询商品
     */
    Product getByBarcode(String barcode);
    
    /**
     * 关键词搜索商品
     */
    List<Product> searchByKeyword(String keyword);
    
    /**
     * 获取低库存商品
     */
    List<Map<String, Object>> getLowStockProducts();
    
    /**
     * 更新库存
     */
    boolean updateStock(Long productId, Integer quantity);
    
    /**
     * 获取库存统计
     */
    Map<String, Object> getStockStatistics();
}