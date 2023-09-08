package com.example.server.repository;

import com.example.server.models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class PostLikeRepositoryTest {
    @Autowired
    private PostLikeRepository postLikeRepository;
    long id;

    @BeforeEach
    void setUp() {
        id=1 ;
    }

    @AfterEach
    void tearDown() {
        postLikeRepository.deleteAll();
    }

    @Test
    void deleteLikeOnPostWhenExists() {
        //given

        //when

        //then
    }

    @Test
    void deleteByIdWhenExists() {
        //given
        PostLike postLike =new PostLike(new Post(
                "hi there this my first post on prezophobia platform",
                "" ,
                new String[]{""}) ,new User(
                "ahmed hamdi",
                "ah2093@gmail.com",
                "ah#123456"
        ), (byte) 1);
        //when
        postLikeRepository.save(postLike);
        //then
    }

    @Test
    void deleteByIdWhenNotExist() {
        //given

        //when

        //then
    }


    @Test
    void checkPostLikesByIdWhenExists() {
        //given
        PostLike postLike =new PostLike(new Post(
                "hi there this my first post on prezophobia platform",
                "" ,
                new String[]{""}) ,new User(
                "ahmed hamdi",
                "ah2093@gmail.com",
                "ah#123456"
        ), (byte) 1);
        //when
        postLikeRepository.save(postLike);
        boolean expected  = postLikeRepository.existsById(id);
        //then
        assertThat(expected).isTrue();

    }
    @Test
    void checkPostLikesByIdWhenDoesNotExist() {
        //when
        boolean expected  = postLikeRepository.existsById(id);
        //then
        assertThat(expected).isFalse();
    }
}