package com.skodin.DTOs;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.skodin.enums.Role;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDTO {

    @Min(1L)
    Long id;

    @Email(message = "email is invalid")
    @NotBlank(message = "email cannot be blank")
    @Max(value = 50, message = "email length must be less than 50 chars")
    String email;

    @NotNull(message = "role cannot be null")
    Role role;

    List<TaskDTO> authoredTasks = new ArrayList<>();
    List<TaskDTO> assignedTasks = new ArrayList<>();
}
