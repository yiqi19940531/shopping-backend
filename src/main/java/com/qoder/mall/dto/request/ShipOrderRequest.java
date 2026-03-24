package com.qoder.mall.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "发货请求")
public class ShipOrderRequest {

    @NotBlank(message = "物流单号不能为空")
    @Schema(description = "物流单号", example = "SF1234567890")
    private String trackingNo;
}
