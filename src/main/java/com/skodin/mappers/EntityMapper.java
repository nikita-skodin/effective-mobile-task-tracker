package com.skodin.mappers;

import com.skodin.DTOs.CommentDTO;
import com.skodin.DTOs.TaskDTO;
import com.skodin.DTOs.UserDTO;
import com.skodin.entities.CommentEntity;
import com.skodin.entities.TaskEntity;
import com.skodin.entities.UserEntity;
import com.skodin.services.TaskService;
import com.skodin.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    private final ModelMapper modelMapper;
    private final UserService userService;
    private final TaskService taskService;

    public EntityMapper(@Lazy TaskService taskService, UserService userService, ModelMapper modelMapper) {
        this.taskService = taskService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    public TaskDTO getDTO(TaskEntity taskEntity) {
        TaskDTO map = modelMapper.map(taskEntity, TaskDTO.class);
        UserEntity assignee = taskEntity.getAssignee();
        if (assignee != null) {
            map.setAssigneeId(assignee.getId());
        }
        map.setAuthorId(taskEntity.getAuthor().getId());
        map.setComments(taskEntity.getComments().stream().map(this::getDTO).toList());
        return map;
    }

    public TaskEntity getEntity(TaskEntity taskEntity, TaskDTO taskDTO) {
        modelMapper.map(taskDTO, taskEntity);
        if (taskDTO.getAssigneeId() != null) {
            taskEntity.setAssignee(userService.findById(taskDTO.getAssigneeId()));
        }
        taskEntity.setAuthor(userService.findById(taskDTO.getAuthorId()));
        taskEntity.setComments(taskDTO.getComments().stream()
                .map(c -> getEntity(new CommentEntity(), c)).toList());
        return taskEntity;
    }

    public CommentDTO getDTO(CommentEntity commentEntity) {
        CommentDTO map = modelMapper.map(commentEntity, CommentDTO.class);
        map.setAuthorId(commentEntity.getAuthor().getId());
        map.setTaskId(commentEntity.getTask().getId());
        return map;
    }

    public CommentEntity getEntity(CommentEntity commentEntity, CommentDTO commentDTO) {
        modelMapper.map(commentDTO, commentEntity);
        commentEntity.setAuthor(userService.findById(commentDTO.getAuthorId()));
        commentEntity.setTask(taskService.findById(commentDTO.getTaskId()));
        return commentEntity;
    }

    public UserEntity getEntity(UserDTO userDTO, UserEntity userEntity) {
        modelMapper.map(userDTO, userEntity);
        return userEntity;
    }

    public UserDTO getDTO(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserDTO.class);
    }

}
