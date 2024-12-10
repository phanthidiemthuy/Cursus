package com.group4.cursus.service;

import com.group4.cursus.entity.Student;
import com.group4.cursus.entity.User;
import com.group4.cursus.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private ClientRegistrationRepository clientRegistrationRepository;

    @Mock
    private OAuth2User oAuth2User;

    private ClientRegistration clientRegistration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the ClientRegistration with authorizationGrantType and redirectUri
        clientRegistration = ClientRegistration.withRegistrationId("test")
                .clientId("test-client")
                .clientSecret("test-secret")
                .authorizationUri("http://localhost/auth")
                .tokenUri("http://localhost/token")
                .userInfoUri("http://localhost/userinfo")
                .userNameAttributeName("email")
                .clientName("Test Client")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost/login/oauth2/code/test")
                .build();
    }

    @Test
    void testGetAllApprovedStudents() {
        User user1 = new User();
        user1.setUserType("STUDENT");
        user1.setApproved(true);

        User user2 = new User();
        user2.setUserType("STUDENT");
        user2.setApproved(true);

        Page<User> users = new PageImpl<>(Arrays.asList(user1, user2));

        when(userRepository.findByUserTypeAndIsApproved("STUDENT", true, PageRequest.of(0, 10))).thenReturn(users);

        Page<User> result = userService.getAllApprovedStudents(0, 10);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("STUDENT", result.getContent().get(0).getUserType());
        assertTrue(result.getContent().get(0).isApproved());
    }

    @Test
    void testBlockUser() {
        User currentUser = new User();
        currentUser.setUserId(1);
        currentUser.setEmail("admin@example.com");

        User userToBlock = new User();
        userToBlock.setUserId(2);

        when(authentication.getName()).thenReturn("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(2)).thenReturn(Optional.of(userToBlock));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        userService.blockUser(2L);

        assertTrue(userToBlock.isBlocked());
        verify(userRepository, times(1)).save(userToBlock);
    }

    @Test
    void testBlockUser_SelfBlock() {
        User currentUser = new User();
        currentUser.setUserId(1);
        currentUser.setEmail("admin@example.com");

        when(authentication.getName()).thenReturn("admin@example.com");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(currentUser));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.blockUser(1L);
        });

        assertEquals("You cannot block yourself", exception.getMessage());
    }

    @Test
    void testUnblockUser() {
        User userToUnblock = new User();
        userToUnblock.setUserId(2);
        userToUnblock.setBlocked(true);

        when(userRepository.findById(2)).thenReturn(Optional.of(userToUnblock));

        userService.unblockUser(2L);

        assertFalse(userToUnblock.isBlocked());
        verify(userRepository, times(1)).save(userToUnblock);
    }

    @Test
    void testIsBlocked() {
        User user = new User();
        user.setUserId(1);
        user.setBlocked(true);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        boolean result = userService.isBlocked(1);

        assertTrue(result);
    }

    @Test
    void testLoadUser_NewUser() {
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "token-value", null, null);
        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, accessToken);

        when(oAuth2User.getAttribute("email")).thenReturn("newuser@example.com");
        when(oAuth2User.getAttribute("name")).thenReturn("New User");
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OAuth2User result = userService.loadUser(userRequest);

        assertEquals("newuser@example.com", result.getAttribute("email"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLoadUser_ExistingUser() {
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "token-value", null, null);
        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, accessToken);

        User existingUser = new User();
        existingUser.setEmail("existinguser@example.com");

        when(oAuth2User.getAttribute("email")).thenReturn("existinguser@example.com");
        when(userRepository.findByEmail("existinguser@example.com")).thenReturn(Optional.of(existingUser));

        OAuth2User result = userService.loadUser(userRequest);

        assertEquals("existinguser@example.com", result.getAttribute("email"));
        verify(userRepository, times(0)).save(any(User.class));
    }
}
