package org.ingsw2526_036.bugboard26backend.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor //per JPA
@DiscriminatorValue("ADMIN")
public class Administrator extends User{
    @OneToMany(mappedBy = "creator")
    private List<Project> projectsCreated;
}
