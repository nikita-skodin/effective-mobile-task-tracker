package com.skodin.mappers;

import com.skodin.DTOs.CommentDTO;
import com.skodin.DTOs.TaskDTO;
import com.skodin.entities.CommentEntity;
import com.skodin.entities.TaskEntity;
import com.skodin.entities.UserEntity;
import com.skodin.services.TaskService;
import com.skodin.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class EntityMapper {

    private final ModelMapper modelMapper;
    private final com.skodin.services.UserService userService;
    private final TaskService taskService;

    public EntityMapper(@Lazy com.skodin.services.TaskService taskService, UserService userService, ModelMapper modelMapper) {
        this.taskService = taskService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    public TaskDTO getDTO(TaskEntity taskEntity) {
        log.debug("Mapping TaskEntity to TaskDTO: {}", taskEntity);
        TaskDTO map = modelMapper.map(taskEntity, TaskDTO.class);
        UserEntity assignee = taskEntity.getAssignee();
        if (assignee != null) {
            map.setAssigneeId(assignee.getId());
        }
        map.setAuthorId(taskEntity.getAuthor().getId());
        map.setComments(taskEntity.getComments().stream().map(this::getDTO).toList());
        log.debug("Mapped TaskDTO: {}", map);
        return map;
    }

    public TaskEntity getEntity(TaskEntity taskEntity, TaskDTO taskDTO) {
        log.debug("Mapping TaskDTO to TaskEntity: {}", taskDTO);
        modelMapper.map(taskDTO, taskEntity);
        if (taskDTO.getAssigneeId() != null) {
            taskEntity.setAssignee(userService.findById(taskDTO.getAssigneeId()));
        }
        taskEntity.setAuthor(userService.findById(taskDTO.getAuthorId()));
        taskEntity.setComments(taskDTO.getComments().stream()
                .map(c -> getEntity(new CommentEntity(), c)).toList());
        log.debug("Mapped TaskEntity: {}", taskEntity);
        return taskEntity;
    }

    public CommentDTO getDTO(CommentEntity commentEntity) {
        log.debug("Mapping CommentEntity to CommentDTO: {}", commentEntity);
        CommentDTO map = modelMapper.map(commentEntity, CommentDTO.class);
        map.setAuthorId(commentEntity.getAuthor().getId());
        map.setTaskId(commentEntity.getTask().getId());
        log.debug("Mapped CommentDTO: {}", map);
        return map;
    }

    public CommentEntity getEntity(CommentEntity commentEntity, CommentDTO commentDTO) {
        log.debug("Mapping CommentDTO to CommentEntity: {}", commentDTO);
        modelMapper.map(commentDTO, commentEntity);
        commentEntity.setAuthor(userService.findById(commentDTO.getAuthorId()));
        commentEntity.setTask(taskService.findById(commentDTO.getTaskId()));
        log.debug("Mapped CommentEntity: {}", commentEntity);
        return commentEntity;
    }
}
