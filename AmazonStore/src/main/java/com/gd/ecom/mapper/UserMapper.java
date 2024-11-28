package com.gd.ecom.mapper;

import com.gd.ecom.entity.User;
import com.gd.ecom.model.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
        User convertToUser(UserDto userDto);
        UserDto convertToUserDto(User user);
}
