package com.gd.ecom.repository;

import com.gd.ecom.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void setUpUser() {
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
    }

    @Test
    @DisplayName("Save User")
    void testSaveUser() {

        User savedUser = userRepository.save(user);


        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();

        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Find User by Email")
    void testFindByEmail() {

        userRepository.save(user);


        Optional<User> userOpt = userRepository.findByEmail("test@example.com");

        assertEquals(1, userRepository.count());
        assertTrue(userOpt.isPresent());
    }
}
