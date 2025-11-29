package org.ingsw2526_036.bugboard26backend.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.ingsw2526_036.bugboard26backend.enums.EditTypeEnum;

import java.sql.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "History")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date date;
    @Column(nullable = false)
    private String oldValue;
    @Column(nullable = false)
    private String newValue;
    @Column(nullable = false)
    private EditTypeEnum editType;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
