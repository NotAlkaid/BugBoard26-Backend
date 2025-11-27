package org.ingsw2526_036.bugboard26backend.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Blob;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Project")
public class Project {
    @Id
    private Long id;
    @Column(unique = true,  nullable = false)
    private String name;
    @Lob
    @Column(nullable = false)
    private Blob icon;
}
