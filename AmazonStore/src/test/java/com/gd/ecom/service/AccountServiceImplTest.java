package com.gd.ecom.service;

import com.gd.ecom.entity.User;
import com.gd.ecom.mapper.UserMapper;
import com.gd.ecom.model.UserDto;
import com.gd.ecom.repository.UserRepository;
import com.gd.ecom.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AccountServiceImplTest {
    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private AuthenticationManager authManager;

    @Autowired
    private AccountServiceImpl accountService;

    @Test
    @DisplayName("Add User - Success")
    void testAddUser_Success() {

        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");


        when(userMapper.convertToUser(userDto)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());


        boolean result = accountService.addUser(userDto);


        assertTrue(result);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).convertToUser(userDto);
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    @DisplayName("Add User - User Already Exists")
    void testAddUser_UserAlreadyExists() {

        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userMapper.convertToUser(userDto)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        boolean result = accountService.addUser(userDto);

        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, times(1)).convertToUser(userDto);
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    @DisplayName("Authenticate - Success")
    void testAuthenticate_Success() {

        String email = "test@example.com";
        String password = "password";

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);

        when(authManager.authenticate(token)).thenReturn(mock(Authentication.class));

        boolean result = accountService.authenticate(email, password);

        assertTrue(result);
        verify(authManager, times(1)).authenticate(token);
    }

    @Test
    @DisplayName("Authenticate - Failure")
    void testAuthenticate_Failure() {

        String email = "test@example.com";
        String password = "wrongpassword";

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);

        when(authManager.authenticate(token)).thenThrow(new RuntimeException("Authentication failed"));

        boolean result = accountService.authenticate(email, password);

        assertFalse(result);
        verify(authManager, times(1)).authenticate(token);
    }
}
