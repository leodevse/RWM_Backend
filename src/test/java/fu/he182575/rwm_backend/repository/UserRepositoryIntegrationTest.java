package fu.he182575.rwm_backend.repository;

import fu.he182575.rwm_backend.common.enums.AccountStatus;
import fu.he182575.rwm_backend.common.enums.UserRole;
import fu.he182575.rwm_backend.entity.UserEntity;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByLoginIdentifierIgnoreCase_shouldReturnSavedUser() {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setLoginIdentifier("staff01");
        user.setPasswordHash("hash");
        user.setRole(UserRole.STAFF);
        user.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        assertTrue(userRepository.findByLoginIdentifierIgnoreCase("STAFF01").isPresent());
    }
}
