package com.legalaid.userauth.service.impl;

import com.legalaid.userauth.dto.request.UserAddressRequest;
import com.legalaid.userauth.dto.response.UserAddressResponse;
import com.legalaid.userauth.entity.User;
import com.legalaid.userauth.entity.UserAddress;
import com.legalaid.userauth.entity.UserAddressId;
import com.legalaid.userauth.exception.AuthExceptions;
import com.legalaid.userauth.repository.UserAddressRepository;
import com.legalaid.userauth.repository.UserRepository;
import com.legalaid.userauth.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;

    @Override
    public UserAddressResponse.Address registerAddress(UserAddressRequest.RegisterUserAddress request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException(email));

        UserAddress userAddress = new UserAddress();
        UserAddressId userAddressId = new UserAddressId(user.getId(), request.getAddressType());

        if (userAddressRepository.existsById(userAddressId)) {
            throw new AuthExceptions.AddressAlreadyExistsException(user.getUsername(), request.getAddressType());
        }

        userAddress.setId(userAddressId);
        userAddress.setUser(user);
        if(request.getDivision() != null) userAddress.setDivision(request.getDivision());
        if(request.getDistrict() != null) userAddress.setDistrict(request.getDistrict());
        if(request.getStreet() != null) userAddress.setStreet(request.getStreet());
        if(request.getPostalCode() != null) userAddress.setPostalCode(request.getPostalCode());
        if(request.getCity() != null) userAddress.setCity(request.getCity());
        userAddressRepository.save(userAddress);

        return buildAddressResponse(userAddress, request.getAddressType());
    }

    @Override
    public UserAddressResponse.Address updateAddress(UserAddressRequest.UpdateUserAddress request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException(email));

        if(request.getAddressType() == null) {
            throw new AuthExceptions.AddressTypeRequiredException();
        }
        UserAddressId addressId = new UserAddressId(user.getId(), request.getAddressType());
        UserAddress userAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new AuthExceptions.AddressNotFoundException(request.getAddressType()));

        if(request.getDivision() != null) userAddress.setDivision(request.getDivision());
        if(request.getDistrict() != null) userAddress.setDistrict(request.getDistrict());
        if(request.getCity() != null) userAddress.setCity(request.getCity());
        if(request.getStreet() != null) userAddress.setStreet(request.getStreet());
        if(request.getPostalCode() != null) userAddress.setPostalCode(request.getPostalCode());
        userAddressRepository.save(userAddress);

        return buildAddressResponse(userAddress, request.getAddressType());
    }

    @Override
    public List<UserAddressResponse.Address> getAddress(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthExceptions.UserNotFoundException(email));

        List<UserAddress> addresses = userAddressRepository.findByUserId(user.getId());
        List<UserAddressResponse.Address> response = new ArrayList<>();
        for (UserAddress address : addresses) {
            response.add(buildAddressResponse(address, address.getId().getAddressType()));
        }
        return response;
    }

    private UserAddressResponse.Address buildAddressResponse(UserAddress request, String addressType) {
        return UserAddressResponse.Address.builder()
                .addressType(addressType)
                .division(request.getDivision())
                .district(request.getDistrict())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .street(request.getStreet())
                .build();
    }
}

