package com.qoder.mall.controller;

import com.qoder.mall.common.result.Result;
import com.qoder.mall.dto.request.AddressRequest;
import com.qoder.mall.entity.Address;
import com.qoder.mall.service.IAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "收货地址", description = "收货地址管理接口")
public class AddressController {

    private final IAddressService addressService;

    @GetMapping
    @Operation(summary = "地址列表")
    public Result<List<Address>> list(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(addressService.getAddresses(userId));
    }

    @PostMapping
    @Operation(summary = "新增地址")
    public Result<Address> add(@Valid @RequestBody AddressRequest request,
                               Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(addressService.addAddress(userId, request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新地址")
    public Result<Void> update(@PathVariable Long id,
                               @Valid @RequestBody AddressRequest request,
                               Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        addressService.updateAddress(userId, id, request);
        return Result.success();
    }

    @PutMapping("/{id}/default")
    @Operation(summary = "设置默认地址")
    public Result<Void> setDefault(@PathVariable Long id,
                                   Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        addressService.setDefault(userId, id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除地址")
    public Result<Void> delete(@PathVariable Long id,
                               Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        addressService.deleteAddress(userId, id);
        return Result.success();
    }
}
