package org.ingsw2526_036.bugboard26backend.states;

import org.ingsw2526_036.bugboard26backend.entities.Issue;

public interface IssueState {
    void next(Issue issue);
    void previous(Issue issue);
}