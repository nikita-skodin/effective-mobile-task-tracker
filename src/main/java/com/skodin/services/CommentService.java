package com.skodin.services;

import com.skodin.DTOs.CommentDTO;
import com.skodin.entities.CommentEntity;
import com.skodin.exceptions.NotFoundException;
import com.skodin.mappers.EntityMapper;
import com.skodin.repositories.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final EntityMapper entityMapper;

    public CommentEntity findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment with id: " + id + " is not found"));
    }

    public List<CommentEntity> findAll() {
        return commentRepository.findAll();
    }

    @Transactional
    public <S extends CommentEntity> S save(S entity) {
        return commentRepository.save(entity);
    }

    @Transactional
    public <S extends CommentEntity> S saveAndFlush(S entity) {
        return commentRepository.saveAndFlush(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new NotFoundException("Comment with id: " + id + " does not exist");
        }
        commentRepository.deleteById(id);
    }

    @Transactional
    public CommentEntity create(CommentDTO commentDTO) {
        CommentEntity commentEntity = entityMapper.getEntity(new CommentEntity(), commentDTO);
        return commentRepository.save(commentEntity);
    }

    @Transactional
    public CommentEntity update(CommentDTO commentDTO) {
        CommentEntity entity = findById(commentDTO.getId());
        entity = entityMapper.getEntity(entity, commentDTO);
        return entity;
    }
}
