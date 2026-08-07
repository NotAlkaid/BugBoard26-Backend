package org.ingsw2526_036.bugboard26backend.states;

import org.ingsw2526_036.bugboard26backend.entities.Issue;
import org.ingsw2526_036.bugboard26backend.enums.StateEnum;

public class InProgressState implements IssueState {
    @Override
    public void next(Issue issue) {
        issue.setState(StateEnum.CLOSED);
    }

    @Override
    public void previous(Issue issue) {
        issue.setState(StateEnum.TODO);
    }
}