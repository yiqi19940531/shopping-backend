package com.qoder.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.qoder.mall.common.exception.BusinessException;
import com.qoder.mall.dto.request.AddressRequest;
import com.qoder.mall.entity.Address;
import com.qoder.mall.mapper.AddressMapper;
import com.qoder.mall.service.IAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements IAddressService {

    private final AddressMapper addressMapper;
    private static final int MAX_ADDRESS_COUNT = 10;

    @Override
    public List<Address> getAddresses(Long userId) {
        return addressMapper.selectList(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .orderByDesc(Address::getIsDefault)
                        .orderByDesc(Address::getCreateTime)
        );
    }

    @Override
    public Address addAddress(Long userId, AddressRequest request) {
        Long count = addressMapper.selectCount(
                new LambdaQueryWrapper<Address>().eq(Address::getUserId, userId)
        );
        if (count >= MAX_ADDRESS_COUNT) {
            throw new BusinessException("收货地址最多" + MAX_ADDRESS_COUNT + "条");
        }

        Address address = new Address();
        address.setUserId(userId);
        copyFromRequest(address, request);
        address.setIsDefault(count == 0 ? 1 : 0);
        addressMapper.insert(address);
        return address;
    }

    @Override
    public void updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = getAndVerifyOwnership(userId, addressId);
        copyFromRequest(address, request);
        addressMapper.updateById(address);
    }

    @Override
    @Transactional
    public void setDefault(Long userId, Long addressId) {
        getAndVerifyOwnership(userId, addressId);
        // Clear all default
        addressMapper.update(null,
                new LambdaUpdateWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .set(Address::getIsDefault, 0)
        );
        // Set new default
        addressMapper.update(null,
                new LambdaUpdateWrapper<Address>()
                        .eq(Address::getId, addressId)
                        .set(Address::getIsDefault, 1)
        );
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        getAndVerifyOwnership(userId, addressId);
        addressMapper.deleteById(addressId);
    }

    private Address getAndVerifyOwnership(Long userId, Long addressId) {
        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("地址不存在");
        }
        return address;
    }

    private void copyFromRequest(Address address, AddressRequest request) {
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetailAddress(request.getDetailAddress());
    }
}
