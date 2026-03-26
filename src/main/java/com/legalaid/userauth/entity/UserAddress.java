package com.legalaid.userauth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "user_addresses")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserAddress {

    @EmbeddedId
    private UserAddressId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "division", length = 20)
    private String division;

    @Column(name = "district", length = 20)
    private String district;

    @Column(name = "city", length = 20)
    private String city;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "street", length = 100)
    private String street;

    @Column(name = "is_visible")
    private boolean isVisible;

    public enum AddressType {
        Home,
        Office,
        Other
    }
}
