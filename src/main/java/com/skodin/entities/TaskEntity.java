package com.skodin.entities;

import com.skodin.enums.Priority;
import com.skodin.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "author_id")
    private UserEntity author;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "assignee_id")
    private UserEntity assignee;

    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<CommentEntity> comments = new ArrayList<>();
}