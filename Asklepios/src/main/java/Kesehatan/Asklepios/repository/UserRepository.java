package Kesehatan.Asklepios.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import Kesehatan.Asklepios.model.User;
import Kesehatan.Asklepios.model.User.Role;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
}
