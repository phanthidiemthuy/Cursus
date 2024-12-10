package com.group4.cursus.config;
import com.group4.cursus.entity.User;
import com.group4.cursus.repository.UserRepository;
import com.group4.cursus.service.StudentService;
import jakarta.servlet.FilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

@Component
public class BlockedUserFilter  extends OncePerRequestFilter {
    @Autowired
    private UserRepository usersRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null  || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }
        String userEmail = authentication.getName();
        User user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isBlocked()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("You have been blocked");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
