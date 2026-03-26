package com.legalaid.userauth.repository;

import com.legalaid.userauth.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

import java.util.*;

public interface UserAddressRepository extends JpaRepository<UserAddress, Serializable> {
    List<UserAddress> findByUserId(UUID userId);
}
