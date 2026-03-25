package com.legalaid.userauth.repository.client;

import org.springframework.data.jpa.repository.JpaRepository;

import com.legalaid.userauth.entity.client.ClientAddress;
import com.legalaid.userauth.entity.client.ClientAddressId;

import java.util.*;

public interface ClientAddressRepository extends JpaRepository<ClientAddress, ClientAddressId> {
    List<ClientAddress> findByIdClientId(UUID clientId);
}
