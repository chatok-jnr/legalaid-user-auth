package com.legalaid.userauth.entity;

import lombok.*;

import java.io.Serializable;

import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class UserAddressId implements Serializable {
    private UUID userId;
    private String addressType;
}

