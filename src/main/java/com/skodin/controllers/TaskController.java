package com.skodin.controllers;

import com.skodin.DTOs.TaskDTO;
import com.skodin.mappers.EntityMapper;
import com.skodin.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Log4j2
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task")
public class TaskController extends MainController {

    private final TaskService taskService;
    private final EntityMapper taskEntityMapper;

    @PostMapping
    @PreAuthorize("@CheckPermissionsService.permitAdmin(#userDetails)")
    public ResponseEntity<TaskDTO> createTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TaskDTO task,
            BindingResult bindingResult) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(taskEntityMapper.getDTO(taskService.create(task)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@CheckPermissionsService.permitAdmin(#userDetails)")
    public ResponseEntity<TaskDTO> updateTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO task,
            BindingResult bindingResult) {
        task.setId(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(taskEntityMapper.getDTO(taskService.update(task)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@CheckPermissionsService.permitAdmin(#userDetails)")
    public ResponseEntity<Boolean> deleteTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        taskService.deleteById(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(true);
    }

}
