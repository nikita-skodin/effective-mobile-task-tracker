package com.skodin.controllers;

import com.skodin.DTOs.CommentDTO;
import com.skodin.entities.UserEntity;
import com.skodin.mappers.EntityMapper;
import com.skodin.services.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController extends MainController {

    private final EntityMapper entityMapper;
    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("@CheckPermissionsService.permitUserToTask(#userDetails, #commentDTO.getTaskId())")
    public ResponseEntity<CommentDTO> createComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CommentDTO commentDTO,
            BindingResult bindingResult) {
        commentDTO.setAuthorId(((UserEntity) userDetails).getId());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(entityMapper.getDTO(commentService.create(commentDTO)));
    }

}
