package org.ingsw2526_036.bugboard26backend.states;

import org.ingsw2526_036.bugboard26backend.entities.Issue;
import org.ingsw2526_036.bugboard26backend.enums.StateEnum;

public class ToDoState implements IssueState {
    @Override
    public void next(Issue issue) {
        issue.setState(StateEnum.INPROGRESS);
    }

    @Override
    public void previous(Issue issue) {
        throw new IllegalStateException("L'Issue è già nello stato iniziale TODO.");
    }
}