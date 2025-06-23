package com.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermarket.entity.Product;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品数据访问层
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 查询所有商品
     */
    @Select("SELECT * FROM products ORDER BY updated_at DESC")
    List<Product> selectAll();

    /**
     * 根据ID查询商品
     */
    @Select("SELECT * FROM products WHERE product_id = #{productId}")
    Product selectById(Long productId);

    /**
     * 根据条码查询商品
     */
    @Select("SELECT * FROM products WHERE barcode = #{barcode} AND status = 'active'")
    Product selectByBarcode(@Param("barcode") String barcode);

    /**
     * 插入商品
     */
    @Insert("INSERT INTO products (product_name, category_id,barcode, price, cost_price, stock_quantity, " +
            "min_stock_level, description, status, created_at, updated_at) VALUES " +
            "(#{productName}, #{categoryId},#{barcode}, #{price}, #{costPrice}, #{stockQuantity}, " +
            "#{minStockLevel}, #{description}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "productId")
    int insert(Product product);

    /**
     * 更新商品信息
     */
    @Update("UPDATE products SET product_name = #{productName}, barcode = #{barcode}, " +
            "price = #{price}, cost_price = #{costPrice}, stock_quantity = #{stockQuantity}, " +
            "min_stock_level = #{minStockLevel}, description = #{description}, " +
            "status = #{status}, updated_at = #{updatedAt} WHERE product_id = #{productId}")
    int update(Product product);

    /**
     * 删除商品
     */
    @Delete("DELETE FROM products WHERE product_id = #{productId}")
    int deleteById(Long productId);

    /**
     * 关键词搜索商品
     */
    @Select("SELECT * FROM products WHERE " +
            "(product_name LIKE CONCAT('%', #{keyword}, '%') OR " +
            "barcode LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND status = 'active' ORDER BY created_at DESC")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 获取低库存商品
     */
    @Select("SELECT product_id, product_name, stock_quantity, min_stock_level, " +
            "(stock_quantity / min_stock_level) as stock_ratio " +
            "FROM products WHERE stock_quantity <= min_stock_level " +
            "AND status = 'active' ORDER BY stock_ratio ASC")
    List<Map<String, Object>> getLowStockProducts();

    /**
     * 统计商品总数
     */
    @Select("SELECT COUNT(*) FROM products WHERE status = 'active'")
    int countActiveProducts();

    /**
     * 统计低库存商品数
     */
    @Select("SELECT COUNT(*) FROM products WHERE stock_quantity <= min_stock_level AND status = 'active'")
    int countLowStockProducts();

    /**
     * 获取库存总价值
     */
    @Select("SELECT SUM(stock_quantity * cost_price) FROM products WHERE status = 'active'")
    BigDecimal getTotalInventoryValue();

    /**
     * 获取热销商品排行
     */

    List<Map<String, Object>> getTopSellingProducts(int limit);

    /**
     * 获取快捷商品（常用商品）
     */
    @Select("SELECT * FROM products WHERE status = 'active' " +
            "ORDER BY updated_at DESC LIMIT 10")
    List<Product> getQuickProducts();

    /**
     * 更新库存
     */
    @Update("UPDATE products SET stock_quantity = stock_quantity + #{quantity}, updated_at = NOW() " +
            "WHERE product_id = #{productId}")
    int updateStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * 批量更新库存
     */
    int batchUpdateStock(List<Map<String, Object>> stockUpdateList);
}