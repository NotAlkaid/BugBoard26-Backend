package org.ingsw2526_036.bugboard26backend.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor //per JPA
@DiscriminatorValue("BASEUSER")
public class BaseUser extends User{
}
