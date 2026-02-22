package com.qoder.mall.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "商品保存请求")
public class ProductSaveRequest {

    @NotBlank(message = "商品名称不能为空")
    @Schema(description = "商品名称", example = "新商品")
    private String name;

    @NotNull(message = "分类不能为空")
    @Schema(description = "分类ID", example = "4")
    private Long categoryId;

    @Schema(description = "品牌", example = "品牌名")
    private String brand;

    @NotNull(message = "价格不能为空")
    @Schema(description = "价格", example = "999.00")
    private BigDecimal price;

    @Schema(description = "原价", example = "1299.00")
    private BigDecimal originalPrice;

    @Schema(description = "库存", example = "100")
    private Integer stock;

    @Schema(description = "封面图片文件ID")
    private Long coverImageId;

    @Schema(description = "简要描述")
    private String description;

    @Schema(description = "富文本详情")
    private String detail;

    @Schema(description = "是否热门(0/1)", example = "0")
    private Integer isHot;

    @Schema(description = "是否推荐(0/1)", example = "0")
    private Integer isRecommend;

    @Schema(description = "轮播图文件ID列表")
    private List<Long> imageFileIds;
}
