package com.relove.repo;

import com.relove.model.dto.UserRoleEnum;
import com.relove.model.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRole(UserRoleEnum admin);
}
