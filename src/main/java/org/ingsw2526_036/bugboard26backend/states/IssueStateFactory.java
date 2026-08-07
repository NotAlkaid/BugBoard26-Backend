package org.ingsw2526_036.bugboard26backend.states;

import org.ingsw2526_036.bugboard26backend.enums.StateEnum;

public class IssueStateFactory {
    
    private static final ToDoState TODO = new ToDoState();
    private static final InProgressState IN_PROGRESS = new InProgressState();
    private static final ClosedState CLOSED = new ClosedState();

    private IssueStateFactory() {
        // Costruttore privato per evitare l'instanziazione
        throw new UnsupportedOperationException("Classe di utilità non istanziabile");
    }
    public static IssueState getState(StateEnum stateEnum) {
        // Gestione null safety
        if (stateEnum == null) return TODO; 

        return switch (stateEnum) {
            case TODO -> TODO;
            case INPROGRESS -> IN_PROGRESS;
            case CLOSED -> CLOSED;
        };
    }
}