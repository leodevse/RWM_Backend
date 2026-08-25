package fu.he182575.rwm_backend.repository;

import fu.he182575.rwm_backend.entity.LoginAuditEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginAuditRepository extends JpaRepository<LoginAuditEntity, UUID> {
}
