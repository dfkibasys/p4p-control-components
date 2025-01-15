package de.dfki.cos.basys.p4p.controlcomponent.binpicking.service;

public class BinPickingStatus {

    public enum MState {
        ACCEPTED, REJECTED, EXECUTING, DONE, NONE, FAILED, ABORTED
    }

    public enum WState {
        OBJECT_DETECTION, POSE_ESTIMATION, PATH_PLANNING, SORTING_PARTS, IDLE, PROVIDING_PARTS, DONE
    }
}
