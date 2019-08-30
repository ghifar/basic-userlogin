package com.ghifar.userlogin.userlogin.repository;

import com.ghifar.userlogin.userlogin.customEnum.RoleName;
import com.ghifar.userlogin.userlogin.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleName name);

}
