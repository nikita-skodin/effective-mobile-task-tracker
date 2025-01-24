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
        log.info("Fetching all tasks with filter: {} and pageable: {}", filter, pageable);
        Page<TaskEntity> tasksPage = taskService.findAllWithPaginationAndFilter(pageable, filter);
        Page<TaskDTO> tasksDTOPage = tasksPage.map(taskEntityMapper::getDTO);
        log.debug("Found {} tasks", tasksPage.getTotalElements());
        return ResponseEntity.ok(tasksDTOPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        log.info("Fetching task with ID: {}", id);
        TaskDTO taskDTO = taskEntityMapper.getDTO(taskService.findById(id));
        log.debug("Fetched task: {}", taskDTO);
        return ResponseEntity.ok(taskDTO);
    }

    @GetMapping("/author")
    public ResponseEntity<Page<TaskDTO>> getTasksByAuthor(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable,
            @RequestParam(required = false) String filter
    ) {
        Long authorId = getUserIdFromPrincipal(userDetails);
        log.info("Fetching tasks for author ID: {} with filter: {} and pageable: {}", authorId, filter, pageable);
        Page<TaskEntity> tasksPage = taskService.findTasksByAuthor(authorId, pageable, filter);
        Page<TaskDTO> tasksDTOPage = tasksPage.map(taskEntityMapper::getDTO);
        log.debug("Found {} tasks for author ID: {}", tasksPage.getTotalElements(), authorId);
        return ResponseEntity.ok(tasksDTOPage);
    }

    @GetMapping("/assignee")
    public ResponseEntity<Page<TaskDTO>> getTasksByAssignee(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable,
            @RequestParam(required = false) String filter
    ) {
        Long assigneeId = getUserIdFromPrincipal(userDetails);
        log.info("Fetching tasks for assignee ID: {} with filter: {} and pageable: {}", assigneeId, filter, pageable);
        Page<TaskEntity> tasksPage = taskService.findTasksByAssignee(assigneeId, pageable, filter);
        Page<TaskDTO> tasksDTOPage = tasksPage.map(taskEntityMapper::getDTO);
        log.debug("Found {} tasks for assignee ID: {}", tasksPage.getTotalElements(), assigneeId);
        return ResponseEntity.ok(tasksDTOPage);
    }

    @PostMapping
    @PreAuthorize("@CheckPermissionsService.permitAdmin(#userDetails)")
    public ResponseEntity<TaskDTO> createTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TaskDTO task,
            BindingResult bindingResult) {
        log.info("Creating new task: {} by user: {}", task, userDetails.getUsername());
        TaskDTO createdTask = taskEntityMapper.getDTO(taskService.create(task));
        log.debug("Task created: {}", createdTask);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createdTask);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@CheckPermissionsService.permitUserToTask(#userDetails, #id)")
    public ResponseEntity<TaskDTO> updateTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO task,
            BindingResult bindingResult) {
        log.info("Updating task with ID: {} by user: {}", id, userDetails.getUsername());
        task.setId(id);
        TaskDTO updatedTask = taskEntityMapper.getDTO(taskService.update(task, userDetails));
        log.debug("Task updated: {}", updatedTask);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedTask);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@CheckPermissionsService.permitAdmin(#userDetails)")
    public ResponseEntity<Boolean> deleteTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        log.info("Deleting task with ID: {} by user: {}", id, userDetails.getUsername());
        taskService.deleteById(id);
        log.info("Task with ID: {} successfully deleted", id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(true);
    }

    private Long getUserIdFromPrincipal(UserDetails userDetails) {
        Long userId = ((UserEntity) userDetails).getId();
        log.debug("Extracted user ID: {} from principal", userId);
        return userId;
    }

}
