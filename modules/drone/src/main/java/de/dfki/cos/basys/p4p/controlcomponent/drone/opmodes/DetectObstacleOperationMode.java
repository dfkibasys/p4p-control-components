package de.dfki.cos.basys.p4p.controlcomponent.drone.opmodes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.*;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@OperationMode(name = "DetectObstacles", shortName = "DETECT", description = "detect nearby obstacles", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class DetectObstacleOperationMode extends BaseDroneOperationMode {

	private CountDownLatch counter;
	@Parameter(name = "waypoints", direction = ParameterDirection.IN)
	private String waypoints = "";
	

	public DetectObstacleOperationMode(BaseControlComponent<DroneService> component) {
		super(component);
	}

	@Override
	public void onStarting() {
		super.onStarting();

		counter = new CountDownLatch(1);

		// start live image listeners
		MissionState.getInstance().addStateListener((oldState, newState) -> {
			if (newState.equals(DroneStatus.MState.ACCEPTED)) {
				component.setErrorStatus(0, "OK");
				counter.countDown();
			}
			else if (newState.equals(DroneStatus.MState.REJECTED)) {
				component.setErrorStatus(3, "rejected");
				counter.countDown();
			}
		});

		// precautionary set timeout error (gets overridden in case of success)
		component.setErrorStatus(4, "timeout");

		// Start Video Streaming with endpoint of obstacle detection service
		getService(DroneService.class).startLiveImage();
		sleep(1000);

		try {
			counter.await(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onExecute() {
		// detect obstacle listener
		MissionState.getInstance().addStateListener((oldState, newState) -> {
			if (newState.equals(DroneStatus.MState.REJECTED)) {
				component.setErrorStatus(2, "aborted");
				component.stop(component.getOccupierId());
			}
		});

		// convert JSON string to List<DronePoint> using Jackson
		List<DronePoint> wp = null;
		try {
			wp = new ObjectMapper().readValue(waypoints, new TypeReference<List<DronePoint>>() {});
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// long polling
		getService(DroneService.class).detectObstacles(wp);
	}

	@Override
	public void onCompleting() {
		super.onCompleting();
		// TODO Report retrieved set of obstacles to Worldmodel server
		sleep(1000);
		// Stop video stream
		getService(DroneService.class).stopLiveImage();
		getService(DroneService.class).reset();
		
	}

	@Override
	public void onStopping() {	
		super.onStopping();
		getService(DroneService.class).stopLiveImage();
		getService(DroneService.class).reset();
	}
}
