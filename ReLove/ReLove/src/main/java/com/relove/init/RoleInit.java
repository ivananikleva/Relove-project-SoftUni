package com.relove.init;

import com.relove.model.dto.UserRoleEnum;
import com.relove.model.entity.RoleEntity;
import com.relove.repo.RoleRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleInit implements CommandLineRunner {

    private final RoleRepo roleRepo;

    public RoleInit(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Override
    public void run(String... args) throws Exception {

        if (roleRepo.count() == 0) {
            RoleEntity adminRole = new RoleEntity();
            adminRole.setRole(UserRoleEnum.ADMIN);

            RoleEntity userRole = new RoleEntity();
            userRole.setRole(UserRoleEnum.USER);

            roleRepo.saveAll(List.of(adminRole, userRole));
        }
    }
}

