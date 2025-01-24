package com.skodin.services;

import com.skodin.DTOs.TaskDTO;
import com.skodin.entities.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public interface TaskService {
    TaskEntity findById(Long aLong);

    Page<TaskEntity> findAllWithPaginationAndFilter(Pageable pageable, String filter);

    @Transactional
    TaskEntity create(TaskDTO taskDTO);

    @Transactional
    TaskEntity update(TaskDTO taskDTO, UserDetails userDetails);

    Page<TaskEntity> findTasksByAuthor(Long authorId, Pageable pageable, String filter);

    Page<TaskEntity> findTasksByAssignee(Long assigneeId, Pageable pageable, String filter);

    @Transactional
    void deleteById(Long id);

    Boolean existsByIdAndAssigneeId(Long id, Long assigneeId);
}
