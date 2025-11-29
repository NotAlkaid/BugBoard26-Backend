package org.ingsw2526_036.bugboard26backend.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "USERTYPE",  discriminatorType = DiscriminatorType.STRING)
@Table(name = "users") 
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Long id;
    @Column(unique = true,  nullable = false)
    @NonNull
    private String username;
    @Column(nullable = false)
    @NonNull
    private String password;
    @Column(unique = true, nullable = false)
    @NonNull
    private String email;
    @OneToMany(mappedBy = "creator")
    private List<Issue> issuesCreated;
    @OneToMany(mappedBy = "assignedTo")
    private List<Issue> issuesAssigned;
    @ManyToMany
    @JoinTable(
            name = "User_Project"
            , joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private List<Project> joinedProjects;
    @OneToMany(mappedBy = "creator")
    private List<Comment> comments;
}
