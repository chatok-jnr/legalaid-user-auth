package com.legalaid.userauth.entity.client;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "client_addresses")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ClientAddress {
    @EmbeddedId
    private ClientAddressId id;

    @MapsId("clientId")
    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "user_id", nullable = false)
    private ClientProfile clientProfile;

    @Column(length = 20)
    private String division;

    @Column(length = 20)
    private String district;

    @Column(length = 20)
    private String city;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(length = 20)
    private String street;

    @Column(name = "is_visible")
    private boolean isVisible = false;

    public LocationType getLocationType() {
        return id != null ? id.getLocationType() : null;
    }
    public void setLocationType(LocationType locationType) {
        if (id == null) id = new ClientAddressId();
        id.setLocationType(locationType);
    }
}
