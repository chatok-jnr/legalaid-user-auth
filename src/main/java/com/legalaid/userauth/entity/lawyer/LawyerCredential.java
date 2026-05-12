package com.legalaid.userauth.entity.lawyer;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "lawyer_credentials")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LawyerCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lawyer_id", nullable = false)
    private LawyerProfile lawyer;

    @Column(name = "credential_type", nullable = false, length = 50)
    private String credentialType;  // 'BAR_CERT', 'DEGREE', 'LICENSE'

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "issuing_body", length = 200)
    private String issuingBody;

    @Column(name = "issued_date")
    private LocalDate issuedDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "document_url", length = 500)
    private String documentUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}