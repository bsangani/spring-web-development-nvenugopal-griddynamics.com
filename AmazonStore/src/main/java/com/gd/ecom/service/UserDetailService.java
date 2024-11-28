package com.gd.ecom.service;

import com.gd.ecom.model.CustomUserDetails;
import com.gd.ecom.entity.User;
import com.gd.ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByEmail(username);
        if (!userOpt.isPresent()) {
            throw new UsernameNotFoundException("Invalid login with user " + username);
        } else return new CustomUserDetails(userOpt.get());
    }
}
