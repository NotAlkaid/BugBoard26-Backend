package org.ingsw2526_036.bugboard26backend.states;

import org.ingsw2526_036.bugboard26backend.entities.Issue;
import org.ingsw2526_036.bugboard26backend.enums.StateEnum;

public class ClosedState implements IssueState {
    @Override
    public void next(Issue issue) {
        throw new IllegalStateException("L'Issue è chiusa e non può avanzare.");
    }

    @Override
    public void previous(Issue issue) {
        issue.setState(StateEnum.INPROGRESS);
    }
}