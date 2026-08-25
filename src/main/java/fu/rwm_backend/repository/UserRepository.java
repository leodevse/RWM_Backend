package fu.he182575.rwm_backend.repository;

import fu.he182575.rwm_backend.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByLoginIdentifierIgnoreCase(String loginIdentifier);

    boolean existsByLoginIdentifierIgnoreCase(String loginIdentifier);
}
