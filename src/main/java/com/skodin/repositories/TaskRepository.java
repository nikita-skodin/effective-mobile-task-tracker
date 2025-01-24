package com.skodin.repositories;

import com.skodin.entities.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    Page<TaskEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM tasks WHERE id = :id AND assignee_id = :assigneeId)", nativeQuery = true)
    Boolean existsByIdAndAssigneeId(@Param("id") Long id, @Param("userId") Long assigneeId);
}
