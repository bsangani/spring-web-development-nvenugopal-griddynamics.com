package com.gd.ecom.service;

import com.gd.ecom.model.UserDto;

public interface AccountService {
    boolean addUser(UserDto userDto);
    boolean authenticate(String email, String password);
}
