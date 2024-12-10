package com.group4.cursus.repository;

import com.group4.cursus.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        // Clear the database before each test
        userRepository.deleteAll();

        // Create and save Users
        user1 = new User("John Doe", "john.doe@example.com", "password123", LocalDate.now(), "123 Main St", false, true, "STUDENT");
        user2 = new User("Jane Smith", "jane.smith@example.com", "password456", LocalDate.now(), "456 Elm St", false, false, "INSTRUCTOR");
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    public void testExistsByEmail() {
        boolean exists = userRepository.existsByEmail("john.doe@example.com");
        assertThat(exists).isTrue();

        exists = userRepository.existsByEmail("nonexistent@example.com");
        assertThat(exists).isFalse();
    }

    @Test
    public void testFindByEmail() {
        Optional<User> user = userRepository.findByEmail("john.doe@example.com");
        assertThat(user).isPresent();
        assertThat(user.get().getFullName()).isEqualTo("John Doe");

        user = userRepository.findByEmail("nonexistent@example.com");
        assertThat(user).isNotPresent();
    }

    @Test
    public void testFindByUserTypeAndIsApproved() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<User> page = userRepository.findByUserTypeAndIsApproved("STUDENT", true, pageable);
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getFullName()).isEqualTo("John Doe");

        page = userRepository.findByUserTypeAndIsApproved("INSTRUCTOR", false, pageable);
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getFullName()).isEqualTo("Jane Smith");

        page = userRepository.findByUserTypeAndIsApproved("STUDENT", false, pageable);
        assertThat(page.getTotalElements()).isEqualTo(0);
    }
}
