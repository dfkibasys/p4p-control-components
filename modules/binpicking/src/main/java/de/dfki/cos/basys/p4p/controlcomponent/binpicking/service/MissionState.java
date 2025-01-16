package de.dfki.cos.basys.p4p.controlcomponent.binpicking.service;

import java.util.ArrayList;
import java.util.List;

public class MissionState {
    BinPickingStatus.MState currentState;
    private final List<MissionStateListener> stateListeners = new ArrayList<>();

    private static final class InstanceHolder {
        static final MissionState INSTANCE = new MissionState();
    }

    private MissionState() {
        this.currentState = BinPickingStatus.MState.NONE;
    }

    public static MissionState getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public BinPickingStatus.MState getState() {
        return this.currentState;
    }

    public void setState(BinPickingStatus.MState newState) {
        BinPickingStatus.MState oldState = this.currentState;
        this.currentState = newState;

        for (MissionStateListener ping : stateListeners) {
            ping.stateChangedEvent(oldState, newState);
        }
    }

    public void addStateListener(MissionStateListener toadd) {
        stateListeners.add(toadd);
    }

    public void removeStateListener(MissionStateListener toremove) {
        stateListeners.remove(toremove);
    }

    public void removeStateListeners() {
        stateListeners.clear();
    }
}
