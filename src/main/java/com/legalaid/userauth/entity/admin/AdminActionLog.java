package com.legalaid.userauth.entity.admin;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.dialect.PostgreSQLInetJdbcType;

import java.net.InetAddress;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "admin_actions_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "action_type", nullable = false, columnDefinition = "admin_action_type")
    private AdminActionType actionType;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    private AdminProfile admin;

    @JdbcType(PostgreSQLInetJdbcType.class)
    @Column(name = "ip_address", nullable = false, columnDefinition = "INET")
    private InetAddress ipAddress;

    @Column(name = "user_agent", nullable = false, columnDefinition = "TEXT")
    private String userAgent;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();
}
