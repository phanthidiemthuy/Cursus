package com.group4.cursus.config;

import com.group4.cursus.entity.Admin;
import com.group4.cursus.repository.AdminRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {


    private AdminRepository adminRepository; // Sử dụng AdminRepository
    private PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner initSuperAdmin() {
        return args -> {
            if (!adminRepository.existsByEmail("adminnehihi")) {
                Admin superAdmin = new Admin();
                superAdmin.setEmail("adminnehihi");
                superAdmin.setPassword(passwordEncoder.encode("strongpassword"));
                superAdmin.setUserType("SUPER_ADMIN");
                superAdmin.setApproved(true);
                superAdmin.setBlocked(false);
                superAdmin.setLevel(3);
                adminRepository.save(superAdmin);
            }
        };
    }
}
