package com.skodin.services;

import com.skodin.DTOs.TaskDTO;
import com.skodin.entities.TaskEntity;
import com.skodin.entities.UserEntity;
import com.skodin.enums.Role;
import com.skodin.exceptions.NotFoundException;
import com.skodin.mappers.EntityMapper;
import com.skodin.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final EntityMapper taskEntityMapper;

    public TaskEntity findById(Long aLong) {
        return taskRepository.findById(aLong)
                .orElseThrow(() -> new NotFoundException("Task with id: " + aLong + " is not found"));
    }

    public List<TaskEntity> findAll() {
        return taskRepository.findAll();
    }

    @Transactional
    public <S extends TaskEntity> S save(S entity) {
        return taskRepository.save(entity);
    }

    @Transactional
    public <S extends TaskEntity> S saveAndFlush(S entity) {
        return taskRepository.saveAndFlush(entity);
    }

    public Page<TaskEntity> findAllWithPaginationAndFilter(Pageable pageable, String filter) {
        if (filter != null && !filter.isEmpty()) {
            return taskRepository.findByTitleContainingIgnoreCase(filter, pageable);
        }
        return taskRepository.findAll(pageable);
    }

    @Transactional
    public TaskEntity create(TaskDTO taskDTO) {
        TaskEntity taskEntity = taskEntityMapper.getEntity(new TaskEntity(), taskDTO);
        return taskRepository.save(taskEntity);
    }

    @Transactional
    public TaskEntity update(TaskDTO taskDTO, UserDetails userDetails) {
        UserEntity user = (UserEntity) userDetails;
        TaskEntity entity = findById(taskDTO.getId());
        taskDTO.setComments(entity.getComments().stream().map(taskEntityMapper::getDTO).toList());
        if (user.getRole() == Role.USER) {
            entity.setStatus(taskDTO.getStatus());
        } else if (user.getRole() == Role.ADMIN) {
            entity = taskEntityMapper.getEntity(entity, taskDTO);
        }
        return entity;
    }

    public Page<TaskEntity> findTasksByAuthor(Long authorId, Pageable pageable, String filter) {
        if (filter != null && !filter.isEmpty()) {
            return taskRepository.findByAuthorIdAndFilter(authorId, filter, pageable);
        }
        return taskRepository.findByAuthorId(authorId, pageable);
    }

    public Page<TaskEntity> findTasksByAssignee(Long assigneeId, Pageable pageable, String filter) {
        if (filter != null && !filter.isEmpty()) {
            return taskRepository.findByAssigneeIdAndFilter(assigneeId, filter, pageable);
        }
        return taskRepository.findByAssigneeId(assigneeId, pageable);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("Task with id: " + id + " does not exist");
        }
        taskRepository.deleteById(id);
    }
}
