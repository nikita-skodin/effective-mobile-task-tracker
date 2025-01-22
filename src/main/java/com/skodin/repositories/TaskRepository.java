package com.skodin.repositories;

import com.skodin.entities.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    Page<TaskEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
