package com.dealflowcrm.backend.repositories;
import com.dealflowcrm.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    List<User> findTop10ByOrderByCreatedAtDesc();

    List<User> findByRole(User.UserRole role);

    List<User> findByStatus(User.UserStatus status);




}
