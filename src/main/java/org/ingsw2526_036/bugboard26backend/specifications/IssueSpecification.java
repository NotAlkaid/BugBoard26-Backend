package org.ingsw2526_036.bugboard26backend.specifications;

import java.util.ArrayList;
import java.util.List;

import org.ingsw2526_036.bugboard26backend.entities.Issue;
import org.ingsw2526_036.bugboard26backend.entities.Label;
import org.ingsw2526_036.bugboard26backend.enums.PriorityEnum;
import org.ingsw2526_036.bugboard26backend.enums.StateEnum;
import org.ingsw2526_036.bugboard26backend.enums.TypeEnum;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

/**
 * Design Pattern: SPECIFICATION (combinato con BUILDER/COMPOSITE)
 *
 * Questa classe implementa il pattern Specification
 * per incapsulare la logica di filtraggio e ricerca dinamica delle Issue sul database,
 * componendo in modo modulare i predicati JPA Criteria senza dover creare query rigide.
 */
public class IssueSpecification {

    public static Specification<Issue> withFilters(Long projectId,
                                                   TypeEnum type,
                                                   StateEnum state,
                                                   PriorityEnum priority,
                                                   Long assignedToId,
                                                   Long labelId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro obbligatorio per progetto
            predicates.add(cb.equal(root.get("project").get("id"), projectId));

            // Filtri opzionali
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (state != null) {
                predicates.add(cb.equal(root.get("state"), state));
            }
            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }
            if (assignedToId != null) {
                predicates.add(cb.equal(root.get("assignedTo").get("id"), assignedToId));
            }
            if (labelId != null) {
                query.distinct(true);
                Join<Issue, Label> labelJoin = root.join("labels");
                predicates.add(cb.equal(labelJoin.get("id"), labelId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
