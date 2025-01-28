package com.skodin.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.skodin.enums.Priority;
import com.skodin.enums.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class TaskDTO {

    @Min(1L)
    Long id;

    @NotBlank(message = "title cannot be blank")
    String title;

    @NotBlank(message = "description cannot be blank")
    String description;

    @NotNull(message = "status cannot be null")
    Status status;

    @NotNull(message = "priority cannot be null")
    Priority priority;

    @Min(1L)
    @NotNull
    Long authorId;

    @Min(1L)
    Long assigneeId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    List<CommentDTO> comments = new ArrayList<>();
}
