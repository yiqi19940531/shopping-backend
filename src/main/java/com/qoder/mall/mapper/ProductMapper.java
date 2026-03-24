package com.qoder.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qoder.mall.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ProductMapper extends BaseMapper<Product> {

    @Update("UPDATE tb_product SET stock = stock - #{quantity}, sales = sales + #{quantity} WHERE id = #{productId} AND stock >= #{quantity} AND is_deleted = 0")
    int deductStock(@Param("productId") Long productId, @Param("quantity") int quantity);

    @Update("UPDATE tb_product SET stock = stock + #{quantity}, sales = sales - #{quantity} WHERE id = #{productId} AND is_deleted = 0")
    int restoreStock(@Param("productId") Long productId, @Param("quantity") int quantity);
}
