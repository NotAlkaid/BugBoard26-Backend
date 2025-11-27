package org.ingsw2526_036.bugboard26backend.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor //per JPA
@DiscriminatorValue("ADMIN")
public class Administrator extends User{
}
