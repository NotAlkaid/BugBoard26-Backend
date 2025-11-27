package org.ingsw2526_036.bugboard26backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.ingsw2526_036.bugboard26backend.enums.PriorityEnum;
import org.ingsw2526_036.bugboard26backend.enums.StateEnum;
import org.ingsw2526_036.bugboard26backend.enums.TypeEnum;
import java.sql.Blob;
import java.sql.Date;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Issue")
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date created;
    @Lob
    private Blob image;
    @Column(nullable = false)
    private PriorityEnum priority;
    @Column(nullable = false)
    private StateEnum state;
    @Column(nullable = false)
    private TypeEnum type;
}
