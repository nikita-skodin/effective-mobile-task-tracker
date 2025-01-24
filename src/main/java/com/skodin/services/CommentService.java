package com.skodin.services;

import com.skodin.DTOs.CommentDTO;
import com.skodin.entities.CommentEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface CommentService {
    @Transactional
    CommentEntity create(CommentDTO commentDTO);
}
