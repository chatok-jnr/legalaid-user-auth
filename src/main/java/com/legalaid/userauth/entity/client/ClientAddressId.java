package com.legalaid.userauth.entity.client;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ClientAddressId implements Serializable {
    private UUID clientId;

    @Enumerated(EnumType.STRING)
    private LocationType locationType;
}

