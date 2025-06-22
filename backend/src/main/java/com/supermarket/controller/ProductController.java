package com.supermarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.entity.Product;
import com.supermarket.service.ProductService;
import com.supermarket.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 分页查询商品
    @GetMapping("/page")
    public Result<IPage<Product>> getPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String productName) {
        try {
            System.out.println("📦 分页查询商品 - 页码:" + current + ", 大小:" + size + ", 商品名:" + productName);
            
            Page<Product> page = new Page<>(current, size);
            QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
            
            if (productName != null && !productName.trim().isEmpty()) {
                queryWrapper.like("product_name", productName.trim());
            }
            
            queryWrapper.eq("status", "active");
            queryWrapper.orderByDesc("created_at");
            
            IPage<Product> productPage = productService.page(page, queryWrapper);
            
            System.out.println("✅ 查询成功，共" + productPage.getTotal() + "条商品记录");
            return Result.success(productPage);
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询商品失败: " + e.getMessage());
        }
    }

    // 创建商品
    @PostMapping
    public Result<Product> create(@RequestBody Product product) {
        try {
            System.out.println("创建商品: " + product.getProductName());
            
            // 检查条码是否已存在
            if (product.getBarcode() != null && !product.getBarcode().trim().isEmpty()) {
                QueryWrapper<Product> checkQuery = new QueryWrapper<>();
                checkQuery.eq("barcode", product.getBarcode());
                long barcodeCount = productService.count(checkQuery);
                if (barcodeCount > 0) {
                    return Result.error("商品条码已存在");
                }
            }
            
            // 设置默认值
            if (product.getStatus() == null) {
                product.setStatus("active");
            }
            if (product.getMinStockLevel() == null) {
                product.setMinStockLevel(10);
            }
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());
            
            boolean success = productService.save(product);
            if (success) {
                System.out.println("✅ 商品创建成功，ID: " + product.getProductId());
                return Result.success(product);
            } else {
                return Result.error("创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建失败: " + e.getMessage());
        }
    }

    // 更新商品
    @PutMapping("/{id}")
    public Result<Product> update(@PathVariable Long id, @RequestBody Product product) {
        try {
            product.setProductId(id);
            product.setUpdatedAt(LocalDateTime.now());
            
            boolean success = productService.updateById(product);
            if (success) {
                return Result.success(product);
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    // 删除商品
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            boolean success = productService.removeById(id);
            if (success) {
                return Result.success();
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    // 根据ID获取商品
    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        try {
            Product product = productService.getById(id);
            
            if (product != null) {
                return Result.success(product);
            } else {
                return Result.error("未找到该商品");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    // 获取所有商品列表
    @GetMapping("/list")
    public Result<List<Product>> getList() {
        try {
            QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", "active");
            queryWrapper.orderByDesc("created_at");
            
            List<Product> products = productService.list(queryWrapper);
            return Result.success(products);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    // 收银台专用：根据条码查询商品
    @GetMapping("/barcode/{barcode}")
    public Result<Product> getByBarcode(@PathVariable String barcode) {
        try {
            System.out.println("🛒 根据条码查询商品: " + barcode);
            
            QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("barcode", barcode);
            queryWrapper.eq("status", "active");
            
            Product product = productService.getOne(queryWrapper);
            
            if (product != null) {
                if (product.getStockQuantity() > 0) {
                    System.out.println("✅ 找到商品: " + product.getProductName());
                    return Result.success(product);
                } else {
                    return Result.error("商品库存不足");
                }
            } else {
                System.out.println("❌ 未找到条码为 " + barcode + " 的商品");
                return Result.error("未找到该商品");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    // 收银台专用：搜索商品
    @GetMapping("/search")
    public Result<List<Product>> searchProducts(@RequestParam String keyword) {
        try {
            System.out.println("🔍 搜索商品: " + keyword);
            
            QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
            queryWrapper.and(wrapper -> wrapper
                .like("product_name", keyword)
                .or()
                .like("barcode", keyword)
            );
            queryWrapper.eq("status", "active");
            queryWrapper.gt("stock_quantity", 0);
            queryWrapper.orderByDesc("created_at");
            queryWrapper.last("LIMIT 20");
            
            List<Product> products = productService.list(queryWrapper);
            
            System.out.println("✅ 搜索到 " + products.size() + " 个商品");
            return Result.success(products);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("搜索失败: " + e.getMessage());
        }
    }
}





