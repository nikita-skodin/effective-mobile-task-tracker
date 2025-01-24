package com.skodin.services;

import com.skodin.DTOs.TaskDTO;
import com.skodin.entities.TaskEntity;
import com.skodin.entities.UserEntity;
import com.skodin.enums.Role;
import com.skodin.exceptions.NotFoundException;
import com.skodin.mappers.EntityMapper;
import com.skodin.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final EntityMapper taskEntityMapper;

    public TaskEntity findById(Long aLong) {
        log.info("Finding task with id: {}", aLong);
        return taskRepository.findById(aLong)
                .orElseThrow(() -> {
                    log.error("Task with id: {} not found", aLong);
                    return new NotFoundException("Task with id: " + aLong + " is not found");
                });
    }

    public Page<TaskEntity> findAllWithPaginationAndFilter(Pageable pageable, String filter) {
        log.info("Fetching tasks with filter: {}", filter);
        if (filter != null && !filter.isEmpty()) {
            return taskRepository.findByTitleContainingIgnoreCase(filter, pageable);
        }
        return taskRepository.findAll(pageable);
    }

    @Transactional
    public TaskEntity create(TaskDTO taskDTO) {
        log.info("Creating task from DTO: {}", taskDTO);
        TaskEntity taskEntity = taskEntityMapper.getEntity(new TaskEntity(), taskDTO);
        TaskEntity savedTask = taskRepository.save(taskEntity);
        log.info("Task created with id: {}", savedTask.getId());
        return savedTask;
    }

    @Transactional
    public TaskEntity update(TaskDTO taskDTO, UserDetails userDetails) {
        log.info("Updating task with id: {}", taskDTO.getId());
        UserEntity user = (UserEntity) userDetails;
        TaskEntity entity = findById(taskDTO.getId());
        taskDTO.setComments(entity.getComments().stream().map(taskEntityMapper::getDTO).toList());

        if (user.getRole() == Role.USER) {
            entity.setStatus(taskDTO.getStatus());
            log.info("User role detected, status updated for task id: {}", taskDTO.getId());
        } else if (user.getRole() == Role.ADMIN) {
            entity = taskEntityMapper.getEntity(entity, taskDTO);
            log.info("Admin role detected, task updated for task id: {}", taskDTO.getId());
        }

        return taskRepository.save(entity);  // Save the updated entity
    }

    public Page<TaskEntity> findTasksByAuthor(Long authorId, Pageable pageable, String filter) {
        log.info("Finding tasks by author with id: {} and filter: {}", authorId, filter);
        if (filter != null && !filter.isEmpty()) {
            return taskRepository.findByAuthorIdAndFilter(authorId, filter, pageable);
        }
        return taskRepository.findByAuthorId(authorId, pageable);
    }

    public Page<TaskEntity> findTasksByAssignee(Long assigneeId, Pageable pageable, String filter) {
        log.info("Finding tasks by assignee with id: {} and filter: {}", assigneeId, filter);
        if (filter != null && !filter.isEmpty()) {
            return taskRepository.findByAssigneeIdAndFilter(assigneeId, filter, pageable);
        }
        return taskRepository.findByAssigneeId(assigneeId, pageable);
    }

    @Transactional
    public void deleteById(Long id) {
        log.info("Attempting to delete task with id: {}", id);
        if (!taskRepository.existsById(id)) {
            log.error("Task with id: {} does not exist", id);
            throw new NotFoundException("Task with id: " + id + " does not exist");
        }
        taskRepository.deleteById(id);
        log.info("Task with id: {} deleted successfully", id);
    }

    public Boolean existsByIdAndAssigneeId(Long id, Long assigneeId) {
        return taskRepository.existsByIdAndAssigneeId(id, assigneeId);
    }
}
