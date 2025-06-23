package com.supermarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermarket.entity.Category;
import com.supermarket.service.CategoryService;
import com.supermarket.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类列表
     */
    @GetMapping("/list")
    public Result<List<Category>> getList() {
        try {
            System.out.println("📂 获取所有分类列表");
            
            QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", "active");
            queryWrapper.orderBy(true, true, "category_id");
            
            List<Category> categories = categoryService.list(queryWrapper);
            
            System.out.println("✅ 获取分类列表成功，共" + categories.size() + "个分类");
            return Result.success("获取成功", categories);
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取分类列表失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询分类
     */
    @GetMapping("/page")
    public Result<IPage<Category>> getPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String categoryName) {
        try {
            System.out.println("📂 分页查询分类 - 页码:" + current + ", 大小:" + size);
            
            Page<Category> page = new Page<>(current, size);
            QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
            
            if (categoryName != null && !categoryName.trim().isEmpty()) {
                queryWrapper.like("category_name", categoryName.trim());
            }
            
            queryWrapper.eq("status", "active");
            queryWrapper.orderByDesc("created_at");
            
            IPage<Category> categoryPage = categoryService.page(page, queryWrapper);
            
            System.out.println("✅ 查询成功，共" + categoryPage.getTotal() + "条记录");
            return Result.success(categoryPage);
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 创建分类
     */
    @PostMapping
    public Result<Category> create(@RequestBody Category category) {
        try {
            System.out.println("➕ 创建分类: " + category.getCategoryName());
            
            category.setStatus("active");
            category.setCreatedAt(LocalDateTime.now());
            category.setUpdatedAt(LocalDateTime.now());
            
            boolean success = categoryService.save(category);
            if (success) {
                System.out.println("✅ 分类创建成功");
                return Result.success("创建成功", category);
            } else {
                return Result.error("创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public Result<Category> update(@PathVariable Long id, @RequestBody Category category) {
        try {
            System.out.println("📝 更新分类: " + id);
            
            category.setCategoryId(id);
            category.setUpdatedAt(LocalDateTime.now());
            
            boolean success = categoryService.updateById(category);
            if (success) {
                System.out.println("✅ 分类更新成功");
                return Result.success("更新成功", category);
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            System.out.println("🗑️ 删除分类: " + id);
            
            boolean success = categoryService.removeById(id);
            if (success) {
                System.out.println("✅ 分类删除成功");
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
