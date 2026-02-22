package com.qoder.mall.service;

import com.qoder.mall.dto.request.AddressRequest;
import com.qoder.mall.entity.Address;

import java.util.List;

public interface IAddressService {

    List<Address> getAddresses(Long userId);

    Address addAddress(Long userId, AddressRequest request);

    void updateAddress(Long userId, Long addressId, AddressRequest request);

    void setDefault(Long userId, Long addressId);

    void deleteAddress(Long userId, Long addressId);
}
