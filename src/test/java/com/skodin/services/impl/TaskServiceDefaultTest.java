package com.skodin.services.impl;

import com.skodin.DTOs.TaskDTO;
import com.skodin.entities.TaskEntity;
import com.skodin.entities.UserEntity;
import com.skodin.enums.Role;
import com.skodin.enums.Status;
import com.skodin.exceptions.NotFoundException;
import com.skodin.mappers.EntityMapper;
import com.skodin.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceDefaultTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private TaskServiceDefault taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById_ShouldReturnTask_WhenExists() {
        Long taskId = 1L;
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

        TaskEntity result = taskService.findById(taskId);

        assertNotNull(result);
        assertEquals(taskId, result.getId());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenTaskDoesNotExist() {
        Long taskId = 1L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taskService.findById(taskId));
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void findAllWithPaginationAndFilter_ShouldReturnFilteredTasks_WhenFilterIsProvided() {
        String filter = "test";
        Pageable pageable = PageRequest.of(0, 10);
        Page<TaskEntity> page = new PageImpl<>(List.of(new TaskEntity()));

        when(taskRepository.findByTitleContainingIgnoreCase(filter, pageable)).thenReturn(page);

        Page<TaskEntity> result = taskService.findAllWithPaginationAndFilter(pageable, filter);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(taskRepository, times(1)).findByTitleContainingIgnoreCase(filter, pageable);
    }

    @Test
    void findAllWithPaginationAndFilter_ShouldReturnAllTasks_WhenFilterIsEmpty() {
        String filter = "";
        Pageable pageable = PageRequest.of(0, 10);
        Page<TaskEntity> page = new PageImpl<>(List.of(new TaskEntity()));

        when(taskRepository.findAll(pageable)).thenReturn(page);

        Page<TaskEntity> result = taskService.findAllWithPaginationAndFilter(pageable, filter);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(taskRepository, times(1)).findAll(pageable);
    }

    @Test
    void create_ShouldSaveAndReturnTaskEntity_WhenDTOIsValid() {
        TaskDTO taskDTO = new TaskDTO();
        TaskEntity taskEntity = new TaskEntity();

        when(entityMapper.getEntity(any(TaskEntity.class), eq(taskDTO))).thenReturn(taskEntity);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);

        TaskEntity result = taskService.create(taskDTO);

        assertNotNull(result);
        verify(entityMapper).getEntity(any(TaskEntity.class), eq(taskDTO));
        verify(taskRepository, times(1)).save(taskEntity);
    }

    @Test
    void update_ShouldUpdateAndReturnTaskEntity_WhenUserIsAdmin() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        TaskEntity taskEntity = new TaskEntity();
        UserEntity adminUser = new UserEntity();
        adminUser.setRole(Role.ADMIN);

        when(taskRepository.findById(taskDTO.getId())).thenReturn(Optional.of(taskEntity));
        when(entityMapper.getEntity(taskEntity, taskDTO)).thenReturn(taskEntity);
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);

        TaskEntity result = taskService.update(taskDTO, adminUser);

        assertNotNull(result);
        verify(taskRepository).findById(taskDTO.getId());
        verify(entityMapper).getEntity(taskEntity, taskDTO);
        verify(taskRepository).save(taskEntity);
    }

    @Test
    void update_ShouldUpdateStatus_WhenUserIsUser() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setStatus(Status.COMPLETED);
        TaskEntity taskEntity = new TaskEntity();
        UserEntity user = new UserEntity();
        user.setRole(Role.USER);

        when(taskRepository.findById(taskDTO.getId())).thenReturn(Optional.of(taskEntity));
        when(taskRepository.save(taskEntity)).thenReturn(taskEntity);

        TaskEntity result = taskService.update(taskDTO, user);

        assertNotNull(result);
        assertEquals(Status.COMPLETED, taskEntity.getStatus());
        verify(taskRepository).findById(taskDTO.getId());
        verify(entityMapper, times(0)).getEntity(taskEntity, taskDTO);
        verify(taskRepository).save(taskEntity);
    }

    @Test
    void findTasksByAuthor_shouldReturnPageOfTasks_whenFilterIsProvided() {
        Long authorId = 1L;
        String filter = "Important";
        PageRequest pageable = PageRequest.of(0, 10);
        TaskEntity task1 = new TaskEntity();
        task1.setId(1L);
        task1.setTitle("Important Task 1");
        TaskEntity task2 = new TaskEntity();
        task2.setId(2L);
        task2.setTitle("Important Task 2");
        Page<TaskEntity> taskPage = new PageImpl<>(List.of(task1, task2));
        when(taskRepository.findByAuthorIdAndFilter(authorId, filter, pageable)).thenReturn(taskPage);

        Page<TaskEntity> result = taskService.findTasksByAuthor(authorId, pageable, filter);

        assertEquals(result, taskPage);
        verify(taskRepository, times(1)).findByAuthorIdAndFilter(authorId, filter, pageable);
    }

    @Test
    void findTasksByAuthor_shouldReturnPageOfTasks_whenFilterIsEmpty() {
        Long authorId = 1L;
        String filter = "";
        PageRequest pageable = PageRequest.of(0, 10);
        TaskEntity task1 = new TaskEntity();
        task1.setId(1L);
        task1.setTitle("Task 1");
        TaskEntity task2 = new TaskEntity();
        task2.setId(2L);
        task2.setTitle("Task 2");
        Page<TaskEntity> taskPage = new PageImpl<>(List.of(task1, task2));
        when(taskRepository.findByAuthorId(authorId, pageable)).thenReturn(taskPage);

        Page<TaskEntity> result = taskService.findTasksByAuthor(authorId, pageable, filter);

        assertEquals(result, taskPage);
        verify(taskRepository, times(0)).findByAuthorIdAndFilter(authorId, filter, pageable);
        verify(taskRepository, times(1)).findByAuthorId(authorId, pageable);
    }

    @Test
    void findTasksByAssignee_shouldReturnPageOfTasks_whenFilterIsProvided() {
        Long assigneeId = 2L;
        String filter = "Urgent";
        PageRequest pageable = PageRequest.of(0, 10);
        TaskEntity task1 = new TaskEntity();
        task1.setId(3L);
        task1.setTitle("Urgent Task 1");
        TaskEntity task2 = new TaskEntity();
        task2.setId(4L);
        task2.setTitle("Urgent Task 2");
        Page<TaskEntity> taskPage = new PageImpl<>(List.of(task1, task2));
        when(taskRepository.findByAssigneeIdAndFilter(assigneeId, filter, pageable)).thenReturn(taskPage);

        Page<TaskEntity> result = taskService.findTasksByAssignee(assigneeId, pageable, filter);

        assertEquals(result, taskPage);
        verify(taskRepository, times(0)).findByAssigneeId(assigneeId, pageable);
        verify(taskRepository, times(1)).findByAssigneeIdAndFilter(assigneeId, filter, pageable);
    }

    @Test
    void findTasksByAssignee_shouldReturnPageOfTasks_whenFilterIsEmpty() {
        Long assigneeId = 2L;
        String filter = "";
        PageRequest pageable = PageRequest.of(0, 10);
        TaskEntity task1 = new TaskEntity();
        task1.setId(5L);
        task1.setTitle("Task 5");
        TaskEntity task2 = new TaskEntity();
        task2.setId(6L);
        task2.setTitle("Task 6");
        Page<TaskEntity> taskPage = new PageImpl<>(List.of(task1, task2));
        when(taskRepository.findByAssigneeId(assigneeId, pageable)).thenReturn(taskPage);

        Page<TaskEntity> result = taskService.findTasksByAssignee(assigneeId, pageable, filter);

        assertEquals(result, taskPage);
        verify(taskRepository, times(0)).findByAssigneeIdAndFilter(assigneeId, filter, pageable);
        verify(taskRepository, times(1)).findByAssigneeId(assigneeId, pageable);
    }

    @Test
    void existsByIdAndAssigneeId_shouldReturnTrue_whenTaskExists() {
        Long taskId = 10L;
        Long assigneeId = 20L;
        when(taskRepository.existsByIdAndAssigneeId(taskId, assigneeId)).thenReturn(true);

        Boolean result = taskService.existsByIdAndAssigneeId(taskId, assigneeId);

        assertTrue(result);
        verify(taskRepository, times(1)).existsByIdAndAssigneeId(taskId, assigneeId);
    }

    @Test
    void existsByIdAndAssigneeId_shouldReturnFalse_whenTaskDoesNotExist() {
        Long taskId = 10L;
        Long assigneeId = 20L;
        when(taskRepository.existsByIdAndAssigneeId(taskId, assigneeId)).thenReturn(false);

        Boolean result = taskService.existsByIdAndAssigneeId(taskId, assigneeId);

        assertFalse(result);
        verify(taskRepository, times(1)).existsByIdAndAssigneeId(taskId, assigneeId);
    }

    @Test
    void deleteById_ShouldDeleteTask_WhenExists() {
        Long taskId = 1L;

        when(taskRepository.existsById(taskId)).thenReturn(true);

        taskService.deleteById(taskId);

        verify(taskRepository).existsById(taskId);
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void deleteById_ShouldThrowNotFoundException_WhenTaskDoesNotExist() {
        Long taskId = 1L;

        when(taskRepository.existsById(taskId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taskService.deleteById(taskId));
        verify(taskRepository).existsById(taskId);
    }
}
