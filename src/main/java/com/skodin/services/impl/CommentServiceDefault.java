package com.skodin.services.impl;

import com.skodin.DTOs.CommentDTO;
import com.skodin.entities.CommentEntity;
import com.skodin.mappers.EntityMapper;
import com.skodin.repositories.CommentRepository;
import com.skodin.services.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceDefault implements CommentService {
    private final CommentRepository commentRepository;
    private final EntityMapper entityMapper;

    @Override
    @Transactional
    public CommentEntity create(CommentDTO commentDTO) {
        log.info("Creating comment from DTO: {}", commentDTO);
        CommentEntity commentEntity = entityMapper.getEntity(new CommentEntity(), commentDTO);
        return commentRepository.save(commentEntity);
    }
}
