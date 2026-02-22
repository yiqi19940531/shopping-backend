package com.qoder.mall.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "商品列表视图对象")
public class ProductVO {

    @Schema(description = "商品ID")
    private Long id;

    @Schema(description = "商品编号")
    private String spuNo;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Schema(description = "库存")
    private Integer stock;

    @Schema(description = "销量")
    private Integer sales;

    @Schema(description = "封面图片URL")
    private String coverImageUrl;

    @Schema(description = "简要描述")
    private String description;

    @Schema(description = "是否热门")
    private Integer isHot;

    @Schema(description = "是否推荐")
    private Integer isRecommend;
}
