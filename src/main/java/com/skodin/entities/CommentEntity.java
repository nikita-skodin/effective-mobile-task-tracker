package com.skodin.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
    @ToString.Exclude
    @JoinColumn(name = "author_id")
    private UserEntity author;

    @ManyToOne()
    @ToString.Exclude
    @JoinColumn(name = "task_id")
    private TaskEntity task;
}