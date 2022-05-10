package de.dfki.cos.basys.p4p.controlcomponent.virtual.service.agv;

import de.dfki.cos.basys.common.rest.mir.dto.SymbolicPosition;
import de.dfki.cos.basys.common.rest.mir.dto.SymbolicPositionInfo;
import de.dfki.cos.mrk40.avro.MissionInstanceInfo;
import de.dfki.cos.mrk40.avro.Pose3d;
import de.dfki.cos.mrk40.avro.Pose3dStamped;

import java.util.List;
import java.util.Map;

public interface AgvService {


    void sendMessage(String message);
    //void sendPose(Pose3dStamped pose);

//    List<SymbolicPosition> getPositions();
//
//    SymbolicPositionInfo getPositionInfo(String positionId);
//
//    MissionInstanceInfo gotoSymbolicPosition(String positionName);
//
//    MissionInstanceInfo getMissionInstanceInfo(int missionInstanceId);

    Map<String,Pose3d> getKnownPoses();

    void gotoKnownPose(String poseId);

    Unit.State getUnitState();

    void setUnitState(Unit.State state);
}
