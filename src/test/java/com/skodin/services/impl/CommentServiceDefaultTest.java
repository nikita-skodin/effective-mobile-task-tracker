package com.skodin.services.impl;

import com.skodin.DTOs.CommentDTO;
import com.skodin.entities.CommentEntity;
import com.skodin.entities.TaskEntity;
import com.skodin.entities.UserEntity;
import com.skodin.mappers.EntityMapper;
import com.skodin.repositories.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CommentServiceDefaultTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private CommentServiceDefault commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldSaveCommentEntity_WhenDTOIsValid() {
        String content = "content";
        CommentDTO commentDTO = new CommentDTO(1L, content, 1L, 1L);
        CommentEntity commentEntity = new CommentEntity(1L, content, new UserEntity(), new TaskEntity());

        when(entityMapper.getEntity(any(CommentEntity.class), eq(commentDTO))).thenReturn(commentEntity);
        when(commentRepository.save(any(CommentEntity.class))).thenReturn(commentEntity);

        CommentEntity savedEntity = commentService.create(commentDTO);

        assertNotNull(savedEntity);
        assertEquals(content, savedEntity.getContent());
        verify(entityMapper, times(1)).getEntity(any(CommentEntity.class), eq(commentDTO));
        verify(commentRepository, times(1)).save(commentEntity);
    }
}
