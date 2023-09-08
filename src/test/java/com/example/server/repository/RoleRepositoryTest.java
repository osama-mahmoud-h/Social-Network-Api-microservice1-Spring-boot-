package com.example.server.repository;

import com.example.server.models.ERole;
import com.example.server.models.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static java.util.Collections.list;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository ;

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
    }

    @Test
    void checkRoleByNameIfExist() {
        //given
        Role role = new Role(ERole.ROLE_USER);
        roleRepository.save(role);
        // when
        Optional<Role> expected = roleRepository.findByName(ERole.ROLE_USER);
        // then
        assertThat(expected.isPresent()).isTrue();
        //assertThat(expected.get().getName()).isEqualTo(ERole.ROLE_USER);
        //assertThat(expected.isEmpty()).isFalse();
    }
    void checkRoleByNameIfNotExist() {
        //given

        // when
        Optional<Role> expected = roleRepository.findByName(ERole.ROLE_USER);
        // then
        assertThat(expected.isPresent()).isFalse();
        //assertThat(expected.get().getName()).isEqualTo(ERole.ROLE_ADMIN);
        //assertThat(expected.isEmpty()).isTrue();
    }
}