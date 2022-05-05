package de.dfki.cos.basys.p4p.controlcomponent.virtual.service.agv;

import de.dfki.cos.basys.common.component.ComponentContext;

import java.io.IOException;
import java.util.Properties;

public class TestMain {

    public static void main(String[] args) {
        Properties config = new Properties();
        config.setProperty("id", "agv_1");
        config.setProperty("topicStatus", "agv_1_status");
        config.setProperty("topicPose", "agv_1_pose");
        config.setProperty("serviceConnectionString", "dockerhost.fritz.box:9092");
        config.setProperty("schemaRegistryUrl", "http://dockerhost.fritz.box:8081");

        AgvServiceImpl service = new AgvServiceImpl(config);
        service.connect(ComponentContext.getStaticContext(), "");

        while (true) {
            service.gotoKnownPose("LID_STORAGE");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            service.gotoKnownPose("LID_JOINING_STATION");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            service.gotoKnownPose("_HOME_");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            service.gotoKnownPose("SCREW_STORAGE");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            service.gotoKnownPose("SCREW_JOINING_STATION");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            service.gotoKnownPose("_HOME_");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
