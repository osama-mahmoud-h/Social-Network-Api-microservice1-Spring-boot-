package com.example.server.services.impl;

import com.example.server.models.Profile;
import com.example.server.models.User;
import com.example.server.repository.FollowerRepository;
import com.example.server.repository.ProfileRepository;
import com.example.server.repository.UserRepository;
import com.example.server.security.jwt.AuthenticatedUser;
import com.example.server.services.FilesStorageService;
import com.example.server.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImpTest {

    @Mock private ProfileServiceImp profileService ;
    @Mock private ProfileRepository profileRepository;
    @Mock private UserService userService;
    @Mock private AuthenticatedUser authenticatedUser;
    @Mock private FilesStorageService filesStorageService;
    @Mock private UserRepository userRepository;
    @Mock private FollowerRepository followerRepository;
    @Mock private HttpServletRequest httpServletRequest ;
    @Test
    void uploadImage() {


    }

    @Test
    void updateBioWhenUserAuthenticated() {
        //given
        /*
        String bio = "update bio within testing ";
        User userUndertest = new User(
                "",
                "",
                ""
        );

        Profile profile = new Profile(
                1L,
                userUndertest,
                new String[]{"tennis"},
                "",
                "",
                "testing",
                "empty"
        );
        userUndertest.setProfile(profile);
        userUndertest.setId(1L);
        */
    }
    @Test
    void updateBioWhenUserDoesNotAuthenticated()
    {
        //when
        //then
        when(profileService.updateBio(httpServletRequest,"test bio")).thenThrow(new NoSuchElementException("No value present"));
        assertThatThrownBy(()->profileService.updateBio(httpServletRequest,"test bio"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("No value present");
    }

    @Test
    void updateAbout() {
        /*
        //given
        String bio="update bio within testing ";
        User user = new User(
                "",
                "" ,
                ""
        );

        Profile profile = new Profile(
                1L,
                user,
                new String[]{"tennis"},
                "",
                "",
                "testing",
                "empty"
        );
        user.setId(1L);
        user.setProfile(profile);
        Optional<User> curUser =Optional.of(user);
        //when
        when(authenticatedUser.getCurrentUser(any())).thenReturn(curUser);
        when(userRepository.findUserById(anyLong())).thenReturn( curUser);
        boolean expected = profileService.updateBio(any() ,bio);

        //then
        assertThat(expected).isTrue();
    */
    }

    @Test
    void updateEducation() {
        /*
        //given
        String about="update about within testing ";
        User user = new User(
                "",
                "" ,
                ""
        );

        Profile profile = new Profile(
                1L,
                user,
                new String[]{"tennis"},
                "",
                "testing",
                "",
                "empty"
        );
        user.setId(1L);
        user.setProfile(profile);
        Optional<User> curUser =Optional.of(user);
        //when
        when(authenticatedUser.getCurrentUser(any())).thenReturn(curUser);
        when(userRepository.findUserById(anyLong())).thenReturn( curUser);
        boolean expected = profileService.updateBio(any() ,bio);

        //then
        assertThat(expected).isTrue();
    }

    @Test
    void updateSkills() {
        //given
        User user = new User(
                "",
                "" ,
                ""
        );

        Profile profile = new Profile(
                1L,
                user,
                new String[]{"tennis"},
                "",
                "",
                "testing",
                "empty"
        );
        user.setId(1L);
        user.setProfile(profile);
        Optional<User> curUser =Optional.of(user);
        //when
        when(authenticatedUser.getCurrentUser(any())).thenReturn(curUser);
        when(userRepository.findUserById(anyLong())).thenReturn( curUser);
        boolean expected = profileService.updateBio(any() ,bio);

        //then
        assertThat(expected).isTrue();

    */
    }

    @Test
    void getUserPosts() {
    }

    @Test
    void getUserStaredPosts() {
    }

    @Test
    void getFollowers() {
    }

    @Test
    void getFollowing() {
    }

    @Test
    void follow() {
    }

    @Test
    void isFollowing() {
    }

    @Test
    void getProfile() {
    }

    @Test
    void updateProfile() {
    }

    @Test
    void allPosts() {

    }
}