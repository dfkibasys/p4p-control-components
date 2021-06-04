package de.dfki.cos.basys.p4p.controlcomponent.drone;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DronePoint;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneService;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.MissionState;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.MissionStateListener;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneStatus.MState;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "MoveWaypoints", shortName = "MOVE_WPT", description = "moves component to waypoints", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class MoveToWaypointsOperationMode extends BaseDroneOperationMode{
	
	private CountDownLatch counter;

	@Parameter(name = "waypoints", direction = ParameterDirection.IN)
	private String waypoints = "";
	
	
	public MoveToWaypointsOperationMode(BaseControlComponent<DroneService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
		
		counter = new CountDownLatch(1);

		// convert JSON string to List<DronePoint> using Jackson
		List<DronePoint> wp = null;
		try {
			wp = new ObjectMapper().readValue(waypoints, new TypeReference<List<DronePoint>>() {});
			//wp = Arrays.asList(new ObjectMapper().readValue(waypoints, DronePoint[].class));
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		MissionState.getInstance().addStateListener(new MissionStateListener() {

			@Override
			public void stateChangedEvent(MState oldState, MState newState) {
				if (newState.equals(MState.ACCEPTED) || newState.equals(MState.EXECUTING)) {
					executing = true;
					component.setErrorStatus(0, "OK");
					counter.countDown();
				}
				else if (newState.equals(MState.REJECTED)) {
					component.setErrorStatus(3, "rejected");
					counter.countDown();
				}
			}
			
		});
		
		// precautionary set timeout error (gets overridden in case of success)
		component.setErrorStatus(4, "timeout");		
		
		getService(DroneService.class).moveToWaypoints(wp);
		
		try {
			counter.await(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onCompleting() {
		super.onCompleting();
		// remove listeners in case of success
		MissionState.getInstance().removeStateListeners();
		sleep(1000);
	}

	@Override
	public void onStopping() {
		super.onStopping();
		// remove listeners in case of error
		MissionState.getInstance().removeStateListeners();
		sleep(1000);
	}
}
