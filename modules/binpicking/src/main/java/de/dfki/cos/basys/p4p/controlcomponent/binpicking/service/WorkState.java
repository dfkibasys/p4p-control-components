package de.dfki.cos.basys.p4p.controlcomponent.binpicking.service;

import java.util.ArrayList;
import java.util.List;

public class WorkState {
    BinPickingStatus.WState currentState;
    private final List<WorkStateListener> stateListeners = new ArrayList<>();

    private static final class InstanceHolder {
        static final WorkState INSTANCE = new WorkState();
    }

    private WorkState() {
        this.currentState = BinPickingStatus.WState.IDLE;
    }

    public static WorkState getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public BinPickingStatus.WState getState() {
        return this.currentState;
    }

    public void setState(BinPickingStatus.WState newState) {
        BinPickingStatus.WState oldState = this.currentState;
        this.currentState = newState;

        for (WorkStateListener ping : stateListeners) {
            ping.stateChangedEvent(oldState, newState);
        }
    }

    public void addStateListener(WorkStateListener toadd) {
        stateListeners.add(toadd);
    }

    public void removeStateListener(WorkStateListener toremove) {
        stateListeners.remove(toremove);
    }

    public void removeStateListeners() {
        stateListeners.clear();
    }
}
