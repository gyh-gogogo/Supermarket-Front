package com.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.entity.Product;
import com.supermarket.mapper.ProductMapper;
import com.supermarket.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public IPage<Product> getPage(int current, int size, String productName) {
        return null;
    }

    @Override
    public Product create(Product product) {
        return null;
    }

    @Override
    public Product update(Product product) {
        return null;
    }

    @Override
    public boolean delete(Long productId) {
        return false;
    }

    @Override
    public List<Product> getAll() {
        return List.of();
    }

    @Override
    public Product getById(Long productId) {
        return null;
    }

    @Override
    public Product getByBarcode(String barcode) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("barcode", barcode);
        queryWrapper.eq("status", "active");
        return this.getOne(queryWrapper);
    }
    

    @Override
    public List<Product> searchByKeyword(String keyword) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper
            .like("product_name", keyword)
            .or()
            .like("barcode", keyword)
        );
        queryWrapper.eq("status", "active");
        queryWrapper.orderByDesc("created_at");
        return this.list(queryWrapper);
    }
    
    @Override
    public List<Map<String, Object>> getLowStockProducts() {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("stock_quantity", "min_stock_level");
        queryWrapper.eq("status", "active");
        queryWrapper.orderBy(true, true, "stock_quantity");
        
        return this.listMaps(queryWrapper);
    }
    
    @Override
    public boolean updateStock(Long productId, Integer quantity) {
        Product product = this.getById(productId);
        if (product != null) {
            int newStock = product.getStockQuantity() + quantity;
            if (newStock >= 0) {
                product.setStockQuantity(newStock);
                return this.updateById(product);
            }
        }
        return false;
    }
    
    @Override
    public Map<String, Object> getStockStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 商品总数
        long totalProducts = this.count(new QueryWrapper<Product>().eq("status", "active"));
        stats.put("totalProducts", totalProducts);
        
        // 低库存商品数
        long lowStockCount = this.count(new QueryWrapper<Product>()
            .le("stock_quantity", "min_stock_level")
            .eq("status", "active"));
        stats.put("lowStockCount", lowStockCount);
        
        // 零库存商品数
        long zeroStockCount = this.count(new QueryWrapper<Product>()
            .eq("stock_quantity", 0)
            .eq("status", "active"));
        stats.put("zeroStockCount", zeroStockCount);
        
        return stats;
    }
}