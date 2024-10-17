package de.dfki.cos.basys.p4p.controlcomponent.binpicking.service;

public class BinPickingStatus {

    public enum MState {
        ACCEPTED, REJECTED, EXECUTING, DONE, PENDING, FAILED, ABORTED
    }

    public enum WState {
        DETECTING, POSE_ESTIMATION, PATH_PLANNING, PICK_AND_PLACE, IDLE
    }
}
