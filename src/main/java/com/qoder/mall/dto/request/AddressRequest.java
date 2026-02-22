package com.qoder.mall.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "收货地址请求")
public class AddressRequest {

    @NotBlank(message = "收货人姓名不能为空")
    @Schema(description = "收货人姓名", example = "张三")
    private String receiverName;

    @NotBlank(message = "收货人电话不能为空")
    @Schema(description = "收货人电话", example = "13800000001")
    private String receiverPhone;

    @NotBlank(message = "省份不能为空")
    @Schema(description = "省份", example = "广东省")
    private String province;

    @NotBlank(message = "城市不能为空")
    @Schema(description = "城市", example = "深圳市")
    private String city;

    @NotBlank(message = "区县不能为空")
    @Schema(description = "区县", example = "南山区")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Schema(description = "详细地址", example = "科技园南路100号")
    private String detailAddress;
}
