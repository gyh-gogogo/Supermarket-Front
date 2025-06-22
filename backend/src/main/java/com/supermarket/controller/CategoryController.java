package com.supermarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.common.Result;
import com.supermarket.entity.Category;
import com.supermarket.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:3000") // 允许前端跨域访问
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping("/page")
    public Result<Page<Category>> getPage(@RequestParam(defaultValue = "1") Integer current,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         @RequestParam(required = false) String categoryName) {
        try {
            Page<Category> page = new Page<>(current, size);
            QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
            
            if (categoryName != null && !categoryName.trim().isEmpty()) {
                queryWrapper.like("category_name", categoryName);
            }
            // 移除status过滤，显示所有分类
            queryWrapper.orderByDesc("created_at");
            
            Page<Category> result = categoryService.page(page, queryWrapper);
            System.out.println("查询分类数据: " + result.getRecords().size() + " 条");
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/list")
    public Result<List<Category>> getList() {
        try {
            QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", "active");
            queryWrapper.orderBy(true, true, "category_name");
            List<Category> result = categoryService.list(queryWrapper);
            
            System.out.println("查询所有分类: " + result.size() + " 条");
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    @PostMapping
    public Result<Category> create(@RequestBody Category category) {
        try {
            System.out.println("创建分类: " + category.getCategoryName());
            
            category.setStatus("active");
            category.setCreatedAt(LocalDateTime.now());
            category.setUpdatedAt(LocalDateTime.now());
            
            boolean success = categoryService.save(category);
            if (success) {
                System.out.println("分类创建成功，ID: " + category.getCategoryId());
                return Result.success(category);
            } else {
                return Result.error("创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public Result<Category> update(@PathVariable Long id, @RequestBody Category category) {
        try {
            System.out.println("更新分类ID: " + id + ", 名称: " + category.getCategoryName());
            
            category.setCategoryId(id);
            category.setUpdatedAt(LocalDateTime.now());
            
            boolean success = categoryService.updateById(category);
            if (success) {
                System.out.println("分类更新成功");
                return Result.success(category);
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        try {
            System.out.println("💥 物理删除分类ID: " + id);
            
            // 检查是否有商品使用此分类
            QueryWrapper<Category> checkQuery = new QueryWrapper<>();
            checkQuery.eq("category_id", id);
            // 这里可以添加检查逻辑，暂时直接删除
            
            // 真正的物理删除 - 直接从数据库中删除记录
            boolean success = categoryService.removeById(id);
            if (success) {
                System.out.println("✅ 分类物理删除成功，数据已从数据库中彻底移除");
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败，分类不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
