package com.group4.cursus.repository;

import com.group4.cursus.entity.Admin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // Use this if you don't want to replace your database with an in-memory database
@ExtendWith(SpringExtension.class)
public class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @BeforeEach
    public void setUp() {
        // Clear the repository before each test
        adminRepository.deleteAll();
    }

    @Test
    public void testExistsByEmail() {
        // Given
        Admin admin = new Admin();
        admin.setEmail("test@example.com");
        adminRepository.save(admin);

        // When
        boolean exists = adminRepository.existsByEmail("test@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    public void testDoesNotExistByEmail() {
        // When
        boolean exists = adminRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isFalse();
    }
}
