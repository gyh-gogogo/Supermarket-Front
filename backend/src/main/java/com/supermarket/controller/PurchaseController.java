package com.supermarket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.common.Result;
import com.supermarket.entity.Purchase;
import com.supermarket.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "http://localhost:3000")
public class PurchaseController {
    
    @Autowired
    private PurchaseService purchaseService;
    
    @GetMapping("/page")
    public Result<Page<Purchase>> getPage(@RequestParam(defaultValue = "1") Integer current,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         @RequestParam(required = false) String purchaseNumber,
                                         @RequestParam(required = false) String supplierName) {
        Page<Purchase> page = new Page<>(current, size);
        return Result.success(purchaseService.getPurchasesWithOperator(page, purchaseNumber, supplierName));
    }
    
    @PostMapping
    public Result<Purchase> create(@RequestBody Purchase purchase) {
        // 生成进货单号
        String purchaseNumber = "P" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        purchase.setPurchaseNumber(purchaseNumber);
        purchase.setPurchaseDate(LocalDateTime.now());
        
        purchaseService.save(purchase);
        return Result.success( purchase);
    }
    
    @GetMapping("/{id}")
    public Result<Purchase> getById(@PathVariable Long id) {
        return Result.success(purchaseService.getById(id));
    }
    
    @PutMapping("/{id}")
    public Result<Purchase> update(@PathVariable Long id, @RequestBody Purchase purchase) {
        purchase.setPurchaseId(id);
        purchaseService.updateById(purchase);
        return Result.success(purchase);
    }
    
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        try {
            System.out.println("💥 物理删除进货记录ID: " + id);
            
            // 真正的物理删除 - 直接从数据库中删除记录
            boolean success = purchaseService.removeById(id);
            if (success) {
                System.out.println("✅ 进货记录物理删除成功，数据已从数据库中彻底移除");
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败，进货记录不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
