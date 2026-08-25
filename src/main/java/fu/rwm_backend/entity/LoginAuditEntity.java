package fu.he182575.rwm_backend.entity;

import fu.he182575.rwm_backend.common.enums.LoginFailureReason;
import fu.he182575.rwm_backend.common.enums.LoginOutcome;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "login_audit_logs",
        uniqueConstraints = @UniqueConstraint(name = "uk_login_audit_event", columnNames = {"id"})
)
public class LoginAuditEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "login_identifier", nullable = false, length = 150)
    private String loginIdentifier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private LoginOutcome outcome;

    @Enumerated(EnumType.STRING)
    @Column(name = "failure_reason", length = 64)
    private LoginFailureReason failureReason;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "source_ip", length = 64)
    private String sourceIp;

    @PrePersist
    void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (occurredAt == null) {
            occurredAt = LocalDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getLoginIdentifier() {
        return loginIdentifier;
    }

    public void setLoginIdentifier(String loginIdentifier) {
        this.loginIdentifier = loginIdentifier;
    }

    public LoginOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(LoginOutcome outcome) {
        this.outcome = outcome;
    }

    public LoginFailureReason getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(LoginFailureReason failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }
}
