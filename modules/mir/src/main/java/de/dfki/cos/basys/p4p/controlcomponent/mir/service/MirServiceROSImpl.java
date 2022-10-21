package de.dfki.cos.basys.p4p.controlcomponent.mir.service;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.common.rest.mir.MiRState;
import de.dfki.cos.basys.common.rest.mir.MirService;
import de.dfki.cos.basys.common.rest.mir.dto.*;

import edu.wpi.rail.jrosbridge.ActionClient;
import edu.wpi.rail.jrosbridge.Goal;
import edu.wpi.rail.jrosbridge.Goal.*;
import edu.wpi.rail.jrosbridge.JRosbridge;
import edu.wpi.rail.jrosbridge.callback.ActionCallback;
import edu.wpi.rail.jrosbridge.messages.actionlib.GoalStatus;
import org.apache.commons.lang3.NotImplementedException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.wpi.rail.jrosbridge.Ros;

import javax.json.Json;
import javax.json.JsonObject;

public class MirServiceROSImpl implements MirService, ServiceProvider<MirService>, MissionHandler {

    protected final Logger LOGGER;
    private Properties config = null;

    private Ros ros = null;
    private String protocol = "ws";
    private String host = "localhost";
    private int port = 9090;

    private ActionClient gotoClient;

    private Status status = new Status();

    private final Map<String, MissionInstanceInfo> missions = new ConcurrentHashMap<>();
    private MissionScheduler scheduler;

    public MirServiceROSImpl(Properties config) {
        this.config = config;
        LOGGER =  LoggerFactory.getLogger(MirServiceROSImpl.class.getName());
        scheduler = new MissionScheduler(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    scheduler.start();
                } catch (InterruptedException ex) {
                    LOGGER.error("Failed to start mission scheduler!");
                }
            }

        }).start();
    }

    @Override
    public boolean connect(ComponentContext context, String connectionString) {
        String patternString = "(?<protocol>wss?):\\/\\/(?<host>.*):(?<port>\\d*)";

        Pattern pattern = Pattern.compile(patternString);

        Matcher matcher = pattern.matcher(connectionString);
        boolean matches = matcher.matches();
        if (!matches) {
            //throw new ComponentException("Invalid external connection string! " + connectionString + " does not match the expected pattern " + patternString);
        }

        protocol = matcher.group("protocol");
        host = matcher.group("host");
        port = Integer.parseInt(matcher.group("port"));

        //TODO: support also wss
        LOGGER.info("Host: " + host + ", port: " + port);
        ros = new Ros(host, port, JRosbridge.WebSocketType.valueOf(protocol));

       if (ros.connect()) {
            String gotoServerName = config.getProperty("gotoServerName");
            String gotoActionName = config.getProperty("gotoActionName");
            gotoClient = new ActionClient(ros, gotoServerName, gotoActionName);
            gotoClient.initialize();
        }

        return ros.isConnected();
    }

    @Override
    public void disconnect() {
        if (ros.isConnected()) {
            gotoClient.dispose();
            ros.disconnect();
        }
    }

    @Override
    public boolean isConnected() {
        return ros == null ? false : ros.isConnected();
    }

    @Override
    public MirService getService() {
        return this;
    }

    @Override
    public Status getRobotStatus() {
        return status;
    }

    @Override
    public Status setRobotStatus(MiRState state) {
        switch(state) {
            case READY:
                // TODO adapt status
                break;
            case PAUSED:
            case ABORTED:
                gotoClient.cancelAll();
                break;
            case EXECUTING:
            case FINISHED:
            case REJECTED:
                break;
            default:
        }

        return status;
    }

    @Override
    public List<MissionDefinition> getMissionDefinitions() {
        throw new NotImplementedException("TODO");
    }

    @Override
    public List<MissionDefinition> getMissionDefinitionsInArea(String areaId) {
        throw new NotImplementedException("TODO");
    }

    @Override
    public List<MissionInstance> getAllMissionInstancesInQueue() {
        return new ArrayList(missions.values());
    }

    @Override
    public MissionInstanceInfo getMissionInstanceInfo(int missionInstanceId) {
        return missions.get(Integer.toString(missionInstanceId));
    }

    @Override
    public synchronized MissionInstanceInfo enqueueMissionInstance(MissionOrder order) {
        MissionInstanceInfo ret = null, mii = new MissionInstanceInfo();
        // Create mission from order
        mii.mission = order.mission_id;
        mii.id = new Random().nextInt();
        mii.ordered =  DateTime.now().getJavaDate();
        mii.parameters = order.parameters;

        mii.state = "REQUESTED";
        missions.put(Integer.toString(mii.id), mii);

        // Try to schedule mission
        if(!scheduler.add(mii)) {  // Did not work: capacity limit of queue exceeded
            mii.message = "Capacity limit of mission queue exceeded!";
            mii.state = "FAILED";
            missions.put(Integer.toString(mii.id), mii);

            //updateMissionState(mii, "FAILED");

        }
        ret = copyMissionInstanceInfo(mii);
        return ret;
    }

    @Override
    public MissionInstanceInfo enqueueMissionInstance(String missionDefinitionId) {
        throw new NotImplementedException("TODO");
    }

    @Override
    public MissionInstanceInfo enqueueMissionInstanceByName(String missionDefinitionName) {
        throw new NotImplementedException("TODO");
    }

    private MissionInstanceInfo copyMissionInstanceInfo(MissionInstanceInfo mii) {
        var ret = new MissionInstanceInfo();
        ret.id = mii.id;
        ret.mission = mii.mission;
        ret.state = mii.state;
        ret.ordered = mii.ordered;
        ret.actions = mii.actions;
        //ret.allowed_methods = mii.allowed_methods;
        //ret.started = mii.started;
        //ret.control_posid = mii.control_posid;
        //ret.created_by_id = mii.created_by_id;
        //ret.control_state = mii.control_state;
        //ret.message = mii.message;
        return ret;
    }

    @Override
    public boolean dequeueMissionInstance(int missionInstanceId) {
        boolean dequeued = false;
        MissionInstanceInfo mii = missions.get(Integer.toString(missionInstanceId));
        if(mii!=null) {
            dequeued = scheduler.remove(mii);
            if(dequeued) { // execution has not yet begun
                LOGGER.info("Sucessfully dequeued mission {}.", mii);
                updateMissionState(mii, "ABORTED");
            }
            else {
                LOGGER.warn("Could not dequeue mission {}. Execution has already begun!", mii);
            }
        }
        else {
            LOGGER.warn("Could not dequeue mission {}. It has not been requested!", missionInstanceId);
        }
        return dequeued;
    }

    @Override
    public List<SymbolicPosition> getPositions() {
        throw new NotImplementedException("TODO");
    }

    @Override
    public List<SymbolicPosition> getMapPositions(String mapId) {
        throw new NotImplementedException("TODO");
    }

    @Override
    public SymbolicPositionInfo getPositionInfo(String positionId) {
        throw new NotImplementedException("TODO");
    }

    @Override
    public List<MissionInstance> getMissionInstancesInQueue() {
        return new ArrayList(missions.values());
    }

    private String convertGoalStatusToMissionState(GoalStatusEnum state) {
        String ret = null;
        switch(state) {
            case RECALLED:
            case PREEMPTED:
            case PREEMPTING:
            case RECALLING:
            case ABORTED:
                ret = "ABORTED";
                break;
            case ACTIVE:
                ret = "EXECUTING";
                break;
            case PENDING:
                ret = "PENDING";
                break;
            case LOST:
            case REJECTED:
                ret = "FAILED";
                break;
            case SUCCEEDED:
                ret = "DONE";
                break;
        }
        return ret;
    }

    @Override
    public MissionInstanceInfo gotoSymbolicPosition(String positionName) {
        // Create mission order of type MOVSYM
        MissionOrder order = new MissionOrder("MOVSYM", "");
        order.priority = 0;
        var params = new ArrayList<Parameter>();
        var pos = new Parameter();
        pos.label = "SymbolicPosition";
        pos.value = positionName;
        params.add(pos);
        order.parameters = params;

        // Enqueue mission
        return enqueueMissionInstance(order);
    }

    @Override
    public MissionInstanceInfo gotoAbsolutePosition(float posX, float posY, float orientation) {
        throw new NotImplementedException("TODO");
    }

    @Override
    public List<SymbolicPosition> getCurrentMapPositions() {
        throw new NotImplementedException("TODO");
    }

    @Override
    public boolean isQueueEmpty() {
        return missions.isEmpty();
    }

    @Override
    public boolean isMissionInQueue(String missionDefinitionId) {
        throw new NotImplementedException("TODO");
    }

    @Override
    public void handleMission(MissionInstanceInfo mii) {
        LOGGER.info("HANDLE_MISSION");
        switch(mii.mission) {
            case "MOVSYM":
            Goal goal = gotoClient.createGoal(new ActionCallback() {

                @Override
                public void handleStatus(GoalStatus gs) {
                    LOGGER.info("GOTO-STATUS: " + gs.toString());
                    //mii.state = convertGoalStatusToMissionState(GoalStatusEnum.get(gs.getStatus()));
                    updateMissionState(mii, convertGoalStatusToMissionState(GoalStatusEnum.get(gs.getStatus())));
                }

                @Override
                public void handleResult(JsonObject result) {
                    LOGGER.info("GOTO-RESULT: " + result.toString());
                }

                @Override
                public void handleFeedback(JsonObject feedback) {
                    LOGGER.info("GOTO-FEEDBK: " + feedback.toString());
                }
            });

            Parameter pos = mii.parameters.stream().filter((Parameter p) -> p.label == "SymbolicPosition").findAny().orElse(null);
            if(pos!=null) {
                JsonObject targetPos = Json.createObjectBuilder().add("target_pos", (String) pos.value).build();
                goal.submit(targetPos);
            }
            break;
        }
    }

    @Override
    public boolean isBusy() {
        return status.state_text == "busy";
    }

    private void updateMissionState(MissionInstanceInfo mii, String newState) {
        mii.state = newState;
        switch(newState) {
            case "EXECUTING":
                status.state_text = "busy";
                mii.started = DateTime.now().getJavaDate();
                break;
            case "DONE":
                status.state_text = "free";
                mii.finished = DateTime.now().getJavaDate();
                break;
            case "PENDING":
                status.state_text = "busy";
                break;
            case "FAILED":
                mii.message = "Failed at " + DateTime.now().getJavaDate();
                status.state_text = "free";
                break;
            case "ABORTED":
                mii.message = "Aborted at " + DateTime.now().getJavaDate();
                status.state_text = "free";
                break;
        }
        missions.put(Integer.toString(mii.id), mii);
    }

}
