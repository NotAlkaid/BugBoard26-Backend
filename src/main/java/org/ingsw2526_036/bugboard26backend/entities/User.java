package org.ingsw2526_036.bugboard26backend.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import org.ingsw2526_036.bugboard26backend.enums.RoleEnum;

@Entity
@Getter
@Setter
@ToString
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
    @ToString.Exclude // Evita loop infiniti
    private List<Issue> issuesCreated;
    @OneToMany(mappedBy = "assignedTo")
    @ToString.Exclude // Evita loop infiniti
    private List<Issue> issuesAssigned;
    @ManyToMany
    @JoinTable(
            name = "User_Project"
            , joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    @ToString.Exclude // Evita loop infiniti
    private List<Project> joinedProjects;
    @OneToMany(mappedBy = "creator")
    @ToString.Exclude // Evita loop infiniti
    private List<Comment> comments;

    public RoleEnum getRole() {
        return (this instanceof Administrator) ? RoleEnum.ADMIN : RoleEnum.BASEUSER;
    }
}
