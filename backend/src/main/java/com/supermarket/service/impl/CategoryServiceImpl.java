package com.supermarket.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.entity.Category;
import com.supermarket.mapper.CategoryMapper;
import com.supermarket.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    
}