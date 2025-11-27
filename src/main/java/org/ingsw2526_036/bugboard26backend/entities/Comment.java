package org.ingsw2526_036.bugboard26backend.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Comment")
public class Comment {
    @Id
    private Long id;
    @Column(nullable = false)
    private String body;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date date;
}
