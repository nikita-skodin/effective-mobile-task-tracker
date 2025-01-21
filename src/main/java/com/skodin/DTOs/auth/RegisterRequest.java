package com.skodin.DTOs.auth;

import com.skodin.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @Email(message = "email is invalid")
    @NotBlank(message = "email cannot be blank")
    private String email;

    private String password;

    private Role role;
}
