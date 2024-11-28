package com.gd.ecom.records;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record LoginRequest(@NotNull(message = "email should not be null") @NotBlank(message = "email should not be empty") String email, @NotNull(message = "password should not be null") @NotBlank(message = "password should not be empty") String password) {
}
