package org.ingsw2526_036.bugboard26backend.specifications;

import java.util.ArrayList;
import java.util.List;

import org.ingsw2526_036.bugboard26backend.dtos.IssueFilterDto;
import org.ingsw2526_036.bugboard26backend.entities.Issue;
import org.ingsw2526_036.bugboard26backend.entities.Label;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Design Pattern: SPECIFICATION (combinato con BUILDER/COMPOSITE)
 *
 * Questa classe implementa il pattern Specification
 * per incapsulare la logica di filtraggio e ricerca dinamica delle Issue sul database,
 * componendo in modo modulare i predicati JPA Criteria senza dover creare query rigide.
 */
public class IssueSpecification {

    private IssueSpecification() {
        // Utility class: costruttore privato per prevenire istanziazione
    }

    public static Specification<Issue> withFilters(Long projectId, IssueFilterDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro obbligatorio per progetto
            predicates.add(cb.equal(root.get("project").get("id"), projectId));

            if (filter != null) {
                applyOptionalFilters(filter, root, query, cb, predicates);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void applyOptionalFilters(IssueFilterDto filter,
                                             Root<Issue> root,
                                             CriteriaQuery<?> query,
                                             CriteriaBuilder cb,
                                             List<Predicate> predicates) {
        if (filter.type() != null) {
            predicates.add(cb.equal(root.get("type"), filter.type()));
        }
        if (filter.state() != null) {
            predicates.add(cb.equal(root.get("state"), filter.state()));
        }
        if (filter.priority() != null) {
            predicates.add(cb.equal(root.get("priority"), filter.priority()));
        }
        if (filter.assignedToId() != null) {
            predicates.add(cb.equal(root.get("assignedTo").get("id"), filter.assignedToId()));
        }
        if (filter.labelId() != null) {
            query.distinct(true);
            Join<Issue, Label> labelJoin = root.join("labels");
            predicates.add(cb.equal(labelJoin.get("id"), filter.labelId()));
        }
    }
}
