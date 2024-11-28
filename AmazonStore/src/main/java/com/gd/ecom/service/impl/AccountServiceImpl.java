package com.gd.ecom.service.impl;

import com.gd.ecom.entity.User;
import com.gd.ecom.exception.UserAlreadyExistsException;
import com.gd.ecom.mapper.UserMapper;
import com.gd.ecom.model.UserDto;
import com.gd.ecom.repository.UserRepository;
import com.gd.ecom.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authManager;

    @Override
    public boolean addUser(UserDto userDto) {
        log.info("Adding new user with email: {}", userDto.getEmail());
        User user = userMapper.convertToUser(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Optional<User> userOpt = userRepository.findByEmail(user.getEmail());
        if (!userOpt.isPresent()) {
            userRepository.save(user);
            log.info("User created successfully with email: {}", user.getEmail());
            return true;
        } else {
            log.warn("User already exists with email: {}", user.getEmail());
            return false;
        }
    }
    @Override
    public boolean authenticate(String email, String password) {
        log.info("Authenticating user with email: {}", email);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        try {
            Authentication authResult = authManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authResult);
            log.info("Authentication successful for email: {}", email);
            return true;
        } catch (Exception e) {
            log.error("Authentication failed for email: {}", email, e);
            return false;
        }
    }
}
