package org.ingsw2526_036.bugboard26backend.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor //per JPA
@DiscriminatorValue("ADMIN")
public class Administrator extends User{
    @OneToMany(mappedBy = "creator")
    @ToString.Exclude // Evita loop infiniti
    private List<Project> projectsCreated;
}
