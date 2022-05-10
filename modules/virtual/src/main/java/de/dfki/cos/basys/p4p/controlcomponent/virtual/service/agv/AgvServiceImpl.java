package de.dfki.cos.basys.p4p.controlcomponent.virtual.service.agv;


import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import de.dfki.cos.basys.common.pathfinding.AStarGrid;
import de.dfki.cos.basys.common.pathfinding.MapUtils;
import de.dfki.cos.basys.p4p.controlcomponent.virtual.service.KafkaProducerFactory;

import de.dfki.cos.mrk40.avro.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;


import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AgvServiceImpl implements  AgvService, ServiceProvider<AgvService>, Unit.Callback {

    private Logger log = LoggerFactory.getLogger(getClass().getName());

    private KafkaProducer<SimpleKey, Pose3dStamped> poseProducer = null;
    private KafkaProducer<String, String> stringProducer = null;

    private final Properties config;
    private final String topicPose, topicStatus;
    private final SimpleKey key;

    private Map<String, Pose3d> knownPositions = null;

    private AStarGrid grid;
    private Unit unit;

    public AgvServiceImpl(Properties config) {
        this.config = config;
        this.topicStatus = config.getProperty("topicStatus");
        this.topicPose = config.getProperty("topicPose");
        this.key = SimpleKey.newBuilder().setKey(config.getProperty("id")).build();

        this.grid = MapUtils.fromResourceStream(getClass().getClassLoader().getResourceAsStream("map_basys3.png"));
        this.grid.setScaleFactor(0.01f);
        this.grid.setOffset(new Vector3f(8.310f,-8.736f,-1.073f));

        Pose3d home = getKnownPoses().get("_HOME_");
        this.unit = new Unit(
                new Vector3f(home.getPosition().getX(), home.getPosition().getY(), home.getPosition().getZ()),
                new Quaternion(home.getOrientation().getX(), home.getOrientation().getY(), home.getOrientation().getZ(), home.getOrientation().getW()),
                grid);
        this.unit.setCallback(this);
    }

    @Override
    public boolean connect(ComponentContext context, String connectionString) {

        try {
            stringProducer = KafkaProducerFactory.createStringProducer(config);
            poseProducer = KafkaProducerFactory.createAvroProducer(config);
            handlePoseChanged(unit.getPosition(), unit.getRotation());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConnected();
    }

    @Override
    public void disconnect() {
        if (stringProducer != null) {
            stringProducer.flush();
            stringProducer.close();
            stringProducer = null;
        }
        if (poseProducer != null) {
            poseProducer.flush();
            poseProducer.close();
            poseProducer = null;
        }
    }

    @Override
    public boolean isConnected() {
        return poseProducer != null;
    }

    @Override
    public AgvService getService() {
        return this;
    }

    @Override
    public void sendMessage(String message) {
        if (stringProducer != null) {
            stringProducer.send(new ProducerRecord<>(topicStatus, message));
        } else {
            // warning
        }
    }

    private void sendPose(Pose3dStamped pose) {
        if (poseProducer != null) {
            poseProducer.send(new ProducerRecord<SimpleKey, Pose3dStamped>(topicPose, key, pose));
        } else {
            // warning
        }
    }

    @Override
    public Map<String, Pose3d> getKnownPoses() {
        if (knownPositions == null) {
            knownPositions = new HashMap<>();
//            Pos 1: (12.457,-8.736,3.559) 	-> Deckelvorratsbehälter
//            Pos 2: (11.203,-8.736,3.559) 	-> Schraubenvorratsbehälter
//            Pos 3: (9.060,-8.736,3.559)	-> Eckpunkt Fahrweg
//            Pos 4: (9.060,-8.736,0.250)	-> Schraubfügestation
//            Pos 5: (9.060,-8.736,-0.873)	-> Deckfügestation
            knownPositions.put("LID_STORAGE",
                    Pose3d.newBuilder()
                            .setPosition(Point3d.newBuilder().setX(12.457f).setY(-8.736f).setZ(3.559f).build())
                            .setOrientation(de.dfki.cos.mrk40.avro.Quaternion.newBuilder().setX(0).setY(1).setZ(0).setW(1).build())
                            .build() );
            knownPositions.put("SCREW_STORAGE",
                    Pose3d.newBuilder()
                            .setPosition(Point3d.newBuilder().setX(11.203f).setY(-8.736f).setZ(3.559f).build())
                            .setOrientation(de.dfki.cos.mrk40.avro.Quaternion.newBuilder().setX(0).setY(1).setZ(0).setW(1).build())
                            .build() );
            knownPositions.put("_HOME_",
                    Pose3d.newBuilder()
                            .setPosition(Point3d.newBuilder().setX(10.060f).setY(-8.736f).setZ(3.559f).build())
                            .setOrientation(de.dfki.cos.mrk40.avro.Quaternion.newBuilder().setX(0).setY(-0.7f).setZ(0).setW(0.7f).build())
                            .build() );
            knownPositions.put("SCREW_JOINING_STATION",
                    Pose3d.newBuilder()
                            .setPosition(Point3d.newBuilder().setX(9.060f).setY(-8.736f).setZ(0.250f).build())
                            .setOrientation(de.dfki.cos.mrk40.avro.Quaternion.newBuilder().setX(0).setY(0).setZ(0).setW(1).build())
                            .build() );
            knownPositions.put("LID_JOINING_STATION",
                    Pose3d.newBuilder()
                            .setPosition(Point3d.newBuilder().setX(9.060f).setY(-8.736f).setZ(-0.873f).build())
                            .setOrientation(de.dfki.cos.mrk40.avro.Quaternion.newBuilder().setX(0).setY(0).setZ(0).setW(1).build())
                            .build() );
        }
        return knownPositions;
    }

    @Override
    public void gotoKnownPose(String poseId) {
        log.info("gotoKnownPose {}", poseId);
        Pose3d pose = getKnownPoses().get(poseId);
        if (pose != null) {

            unit.gotoPositionAsync(
                    new Vector3f(pose.getPosition().getX(),pose.getPosition().getY(),pose.getPosition().getZ()),
                    new Quaternion(pose.getOrientation().getX(), pose.getOrientation().getY(), pose.getOrientation().getZ(), pose.getOrientation().getW())
            );
        } else {
            // set error state
            // throw exception
        }
    }

    @Override
    public Unit.State getUnitState() {
        return unit.getState();
    }

   @Override
    public void setUnitState(Unit.State state) {
        unit.setState(state);
    }

    @Override
    public void handleStateChange(Unit.State state) {
        sendMessage("AgvService.handleStateChange: " + state);
    }

    @Override
    public void handlePathFound(Path path) {

    }

    @Override
    public void handlePoseChanged(Vector3f position, Quaternion orientation) {
        Instant now = Instant.now();
        var pose = Pose3dStamped.newBuilder()
                .setPose(Pose3d.newBuilder()
                        .setPosition(Point3d.newBuilder().setX(position.x).setY(position.y).setZ(position.z).build())
                        .setOrientation(de.dfki.cos.mrk40.avro.Quaternion.newBuilder().setX(orientation.getX()).setY(orientation.getY()).setZ(orientation.getZ()).setW(orientation.getW()).build())
                        .build())
                .setTimestamp(TimestampUnix.newBuilder().setSeconds(now.getEpochSecond()).setNseconds(now.getNano()).build())
                .build();
        sendPose(pose);
    }


//    public Pose3dStamped resetCurrentPose() {
//        Instant now = Instant.now();
//        currentPose = Pose3dStamped.newBuilder()
//                .setPose(Pose3d.newBuilder()
//                        .setPosition(Point3d.newBuilder().setX(12.457f).setY(-8.736f).setZ(3.559f).build())
//                        .setOrientation(Quaternion.newBuilder().setX(0).setY(0).setZ(0).setW(1).build())
//                        .build())
//                .setTimestamp(TimestampUnix.newBuilder().setSeconds(now.getEpochSecond()).setNseconds(now.getNano()).build())
//                .build();
//        return currentPose;
//    }


}
