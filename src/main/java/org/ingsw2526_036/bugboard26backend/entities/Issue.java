package org.ingsw2526_036.bugboard26backend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.ingsw2526_036.bugboard26backend.enums.PriorityEnum;
import org.ingsw2526_036.bugboard26backend.enums.StateEnum;
import org.ingsw2526_036.bugboard26backend.enums.TypeEnum;
import java.sql.Blob;
import java.sql.Date;


@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "Issue")
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Long id;
    @Column(nullable = false)
    @NonNull
    private String title;
    @Column(nullable = false)
    @NonNull
    private String description;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    @NonNull
    private Date created;
    @Lob
    private Blob image;
    @Column(nullable = false)
    @NonNull
    private PriorityEnum priority;
    @Column(nullable = false)
    @NonNull
    private StateEnum state;
    @Column(nullable = false)
    @NonNull
    private TypeEnum type;
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    @NonNull
    private User creator;
    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignedTo;
    @ManyToOne
    @JoinColumn(name ="project_id", nullable = false)
    @NonNull
    private Project project;
}
