package com.skodin.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "comments")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne()
    @JoinColumn(name = "author_id")
    private UserEntity author;

    @ManyToOne()
    @JoinColumn(name = "task_id")
    private TaskEntity task;
}