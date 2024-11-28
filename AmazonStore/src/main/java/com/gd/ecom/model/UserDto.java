package com.gd.ecom.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @Email(message = "Email format is invalid")
    @NotBlank(message = "Email should not be blank")
    @NotNull(message = "Email should not be null")
    private String email;
    @NotNull(message = "Password should not be null")
    @Size(min = 8, message = "Password should be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password should contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;
}

