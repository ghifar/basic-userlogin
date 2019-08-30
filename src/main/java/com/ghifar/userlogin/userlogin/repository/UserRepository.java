package com.ghifar.userlogin.userlogin.repository;

import com.ghifar.userlogin.userlogin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findByUsername(String username);

    List<User> findByIdIn(List<Long> userIds);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}

