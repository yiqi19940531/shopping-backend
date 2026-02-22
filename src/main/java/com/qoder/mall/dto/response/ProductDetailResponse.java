package com.qoder.mall.dto.response;

import com.qoder.mall.vo.ProductVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品详情响应")
public class ProductDetailResponse extends ProductVO {

    @Schema(description = "富文本详情")
    private String detail;

    @Schema(description = "轮播图URL列表")
    private List<String> imageUrls;
}
