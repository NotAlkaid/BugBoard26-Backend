package org.ingsw2526_036.bugboard26backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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
    @Column(nullable = false)
    private byte @NonNull [] icon; // jpa store byte array as blob
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    @NonNull
    private Administrator creator;
    @ManyToMany(mappedBy = "joinedProjects")
    @ToString.Exclude // Evita loop infiniti
    private List<User> participants = new ArrayList<>();
    @OneToMany(mappedBy = "project")   
    @ToString.Exclude // Evita loop infiniti
    private List<Issue> issues;
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        return id.equals(((Project) o).getId());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
