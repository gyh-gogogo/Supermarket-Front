package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.entity.SaleItem;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 销售明细数据访问层
 */
@Mapper
public interface SaleItemMapper extends BaseMapper<SaleItem> {

    /**
     * 根据销售ID获取销售明细
     */
    List<Map<String, Object>> getSaleItemsBySaleId(Long saleId);

    /**
     * 根据商品ID获取销售明细
     */
    List<Map<String, Object>> getSaleItemsByProductId(Long productId);

    /**
     * 删除销售明细
     */
    int deleteBySaleId(Long saleId);

    /**
     * 获取商品销售统计
     */
    Map<String, Object> getProductSalesStats(Long productId);

    /**
     * 获取热销商品排行
     */
    List<Map<String, Object>> getTopSellingProducts(Map<String, Object> params);
}
