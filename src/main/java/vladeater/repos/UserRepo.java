package vladeater.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vladeater.domain.User;

/**
 * @author Vlados Guskov
 */

@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    User findByUsername(String userName);
    User findByActivationCode(String code);
}
