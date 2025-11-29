package org.ingsw2526_036.bugboard26backend.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String body;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date date;
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    @NonNull
    private User creator;
}
