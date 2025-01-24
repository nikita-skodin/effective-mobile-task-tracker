package com.skodin.controllers;

import com.skodin.DTOs.TaskDTO;
import com.skodin.entities.TaskEntity;
import com.skodin.entities.UserEntity;
import com.skodin.mappers.EntityMapper;
import com.skodin.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping
    public ResponseEntity<Page<TaskDTO>> getAllTasks(
            Pageable pageable,
            @RequestParam(required = false) String filter
    ) {
        Page<TaskEntity> tasksPage = taskService.findAllWithPaginationAndFilter(pageable, filter);
        Page<TaskDTO> tasksDTOPage = tasksPage.map(taskEntityMapper::getDTO);
        return ResponseEntity.ok(tasksDTOPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskEntityMapper.getDTO(taskService.findById(id)));
    }

    @GetMapping("/author")
    public ResponseEntity<Page<TaskDTO>> getTasksByAuthor(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable,
            @RequestParam(required = false) String filter
    ) {
        Long authorId = getUserIdFromPrincipal(userDetails); // Метод для получения ID пользователя
        Page<TaskEntity> tasksPage = taskService.findTasksByAuthor(authorId, pageable, filter);
        Page<TaskDTO> tasksDTOPage = tasksPage.map(taskEntityMapper::getDTO);
        return ResponseEntity.ok(tasksDTOPage);
    }

    @GetMapping("/assignee")
    public ResponseEntity<Page<TaskDTO>> getTasksByAssignee(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable,
            @RequestParam(required = false) String filter
    ) {
        Long assigneeId = getUserIdFromPrincipal(userDetails); // Метод для получения ID пользователя
        Page<TaskEntity> tasksPage = taskService.findTasksByAssignee(assigneeId, pageable, filter);
        Page<TaskDTO> tasksDTOPage = tasksPage.map(taskEntityMapper::getDTO);
        return ResponseEntity.ok(tasksDTOPage);
    }

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
    @PreAuthorize("@CheckPermissionsService.permitUserToTask(#userDetails, #id)")
    public ResponseEntity<TaskDTO> updateTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO task,
            BindingResult bindingResult) {
        task.setId(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(taskEntityMapper.getDTO(taskService.update(task, userDetails)));
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

    private Long getUserIdFromPrincipal(UserDetails userDetails) {
        UserEntity u = (UserEntity) userDetails;
        return u.getId();
    }

}
