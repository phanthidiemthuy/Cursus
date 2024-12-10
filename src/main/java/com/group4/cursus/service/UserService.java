package com.group4.cursus.service;


import com.group4.cursus.entity.Student;
import com.group4.cursus.entity.User;
import com.group4.cursus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

//    public List<User> findAllUsersByTypeAndApproval(String userType, boolean isApproved) {
//        return userRepository.findByUserTypeAndIsApproved(userType, isApproved);
//    }

    public Page<User> getAllApprovedStudents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByUserTypeAndIsApproved("STUDENT", true, pageable);
    }

    public Page<User> getAllApprovedInstructor(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByUserTypeAndIsApproved("INSTRUCTOR", true, pageable);
    }

    public Page<User> getAllApprovedInstructorPending(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByUserTypeAndIsApproved("INSTRUCTOR", false, pageable);
    }


    public void blockUser(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        if (currentUser.getUserId() == id) {
            throw new RuntimeException("You cannot block yourself");
        }
        User user = userRepository.findById(Math.toIntExact(id)).orElseThrow(() -> new RuntimeException("User not found"));
        user.setBlocked(true);
        userRepository.save(user);
    }

    public void unblockUser(Long id) {
        User user = userRepository.findById(Math.toIntExact(id)).orElseThrow(() -> new RuntimeException("User not found"));
        user.setBlocked(false);
        userRepository.save(user);
    }

    public boolean isBlocked(int adminId) {
        User user = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.isBlocked();
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");
        Optional<User> userOptional = userRepository.findByEmail(email);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = new Student();
            user.setFullName(oAuth2User.getAttribute("name"));
            user.setEmail(email);
            user.setRegistrationDate(LocalDate.now());
            user.setAddress("");
            user.setBlocked(false);
            user.setApproved(true);
            user.setUserType("STUDENT");
            userRepository.save(user);
        }

        return oAuth2User;
    }


}


