package org.ingsw2526_036.bugboard26backend.entities;


import jakarta.persistence.*;
import lombok.*;
import java.sql.Blob;
import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "Project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Long id;
    @Column(unique = true,  nullable = false)
    @NonNull
    private String name;
    @Lob
    @Column(nullable = false)
    @NonNull
    private Blob icon;
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    @NonNull
    private Administrator creator;
    @ManyToMany(mappedBy = "joinedProjects")
    private List<User> participants;
    @OneToMany(mappedBy = "project")
    private List<Issue> issues;
}
