package com.qoder.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String spuNo;

    private String name;

    private Long categoryId;

    private String brand;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private Integer sales;

    private Long coverImageId;

    private String description;

    private String detail;

    private Integer status;

    private Integer isHot;

    private Integer isRecommend;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
