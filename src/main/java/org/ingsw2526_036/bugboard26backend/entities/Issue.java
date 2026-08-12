package org.ingsw2526_036.bugboard26backend.entities;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.ingsw2526_036.bugboard26backend.enums.PriorityEnum;
import org.ingsw2526_036.bugboard26backend.enums.StateEnum;
import org.ingsw2526_036.bugboard26backend.enums.TypeEnum;
import org.ingsw2526_036.bugboard26backend.states.IssueState;
import org.ingsw2526_036.bugboard26backend.states.IssueStateFactory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "Issue")
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Long id;
    @Column(nullable = false)
    @NonNull
    private String title;
    @Column(nullable = false)
    @NonNull
    private String description;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    @NonNull
    private Date creationDate;
    @Column(nullable = true)
    private byte[] image;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NonNull
    private PriorityEnum priority;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NonNull
    private StateEnum state;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NonNull
    private TypeEnum type;
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    @NonNull
    private User creator;
    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;
    @ManyToOne
    @JoinColumn(name ="project_id", nullable = false)
    @NonNull
    private Project project;
    @OneToMany(mappedBy = "issue")
    @ToString.Exclude
    private List<Comment> comments;
    @ManyToMany
    @JoinTable(
            name = "issue_label",
            joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    @ToString.Exclude
    private Set<Label> labels = new HashSet<>();

    // Pattern STATE: Metodi di Business Logic
    public void promote() {
        IssueState currentStateObj = IssueStateFactory.getState(this.state);
        currentStateObj.next(this);
    }

    public void demote() {
        IssueState currentStateObj = IssueStateFactory.getState(this.state);
        currentStateObj.previous(this);
    }

}