package com.legalaid.userauth.service;

import com.legalaid.userauth.dto.request.UserAddressRequest;
import com.legalaid.userauth.dto.response.UserAddressResponse;

import java.util.*;

public interface UserAddressService {
    UserAddressResponse.Address registerAddress(UserAddressRequest.RegisterUserAddress request, String email);
    UserAddressResponse.Address updateAddress(UserAddressRequest.UpdateUserAddress request, String email);
    List<UserAddressResponse.Address> getAddress(String email);
}
