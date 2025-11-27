package org.ingsw2526_036.bugboard26backend.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
}
