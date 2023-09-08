package com.example.server.repository;

import com.example.server.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    User user ;
    String email ;
    long id;

    @BeforeEach
    void setUp() {
        email = "ah2093@gmail.com";
        user = new User(
                "ahmed hamdi",
                email,
                "123456"
        );
        id =1 ;
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void checkUserByEmailWhenExists() {

        //given
        //when
        userRepository.save(user);
        Optional<User> expected=  userRepository.findUserByEmail(email);
        //then
        assertThat(expected.isPresent()).isTrue();
        //assertThat(expected.get().hashCode()).isEqualTo(user.hashCode());
        //you can assert on any property of user

    }
    @Test
    void checkUserByEmailWhenNotExist() {

        //given
        //when
        Optional<User> expected=  userRepository.findUserByEmail(email);
        //then
        assertThat(expected.isPresent()).isFalse();
    }

    @Test
    void checkUserByIdWhenExists() {
        //given
        //as it will be the first record to store will take id = 1
        //when
        userRepository.save(user);
        Optional<User> expected=  userRepository.findUserById(id);
        //then
        assertThat(expected.get().getId()).isEqualTo(id);
        //assertThat(expected.isPresent()).isTrue();
        //assertThat(expected.get().hashCode()).isEqualTo(user.hashCode());
        //you can assert on any property of user

    }
    @Test
    void checkUserByIdWhenNotExist() {
        //given
        //when
        Optional<User> expected=  userRepository.findUserById(id);
        //then
        assertThat(expected.isPresent()).isFalse();
    }

    @Test
    void checkUserExistenceByEmailWhenExist() {
        //given
        //when
        userRepository.save(user);
        Boolean expected=  userRepository.existsByEmail(email);
        //then
        assertThat(expected).isTrue();
    }
    @Test
    void checkUserExistenceByEmailWhenNotExist() {
        //given
        //when
        Boolean expected=  userRepository.existsByEmail(email);
        //then
        assertThat(expected).isFalse();
    }
}