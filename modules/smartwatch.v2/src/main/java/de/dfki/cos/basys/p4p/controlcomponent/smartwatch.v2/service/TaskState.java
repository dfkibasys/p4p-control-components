package de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.service;

import java.util.ArrayList;
import java.util.List;

import de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.service.SmartwatchStatus.TState;

public class TaskState {

    TState currentState;
    private final List<TaskStateListener> stateListeners = new ArrayList<>();

    private static final class InstanceHolder {
        static final TaskState INSTANCE = new TaskState();
    }

    private TaskState() {
        this.currentState = TState.NONE;
    }

    public static TaskState getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public TState getState() {
        return this.currentState;
    }

    public void setState(TState newState) {
        TState oldState = this.currentState;
        this.currentState = newState;

        for (TaskStateListener ping : stateListeners) {
            ping.stateChangedEvent(oldState, newState);
        }
    }

    public void addStateListener(TaskStateListener toadd) {
        stateListeners.add(toadd);
    }

    public void removeStateListener(TaskStateListener toremove) {
        stateListeners.remove(toremove);
    }

    public void removeStateListeners() {
        stateListeners.clear();
    }

}
