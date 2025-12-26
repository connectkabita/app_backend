package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    // Required for login logic
    Optional<User> findByUsernameOrEmail(String username, String email);

    // Required for forgot password and reset token logic
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String token);
}