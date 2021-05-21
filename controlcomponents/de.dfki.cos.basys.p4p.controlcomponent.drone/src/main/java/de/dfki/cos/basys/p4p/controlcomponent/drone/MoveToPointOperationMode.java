package de.dfki.cos.basys.p4p.controlcomponent.drone;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DronePoint;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneService;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneStatus.MissionState;

import java.sql.Date;
import java.util.concurrent.TimeUnit;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "MovePoint", shortName = "MOVE_PNT", description = "moves component to a point", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class MoveToPointOperationMode extends BaseDroneOperationMode{
	private static final int NUM_RETRIES = 20;
	private static final Logger LOG = LoggerFactory.getLogger(MoveToPointOperationMode.class);
	
	@Parameter(name = "x", direction = ParameterDirection.IN)
	private float x = 0.0f;
	
	@Parameter(name = "y", direction = ParameterDirection.IN)
	private float y = 0.0f;
	
	@Parameter(name = "z", direction = ParameterDirection.IN)
	private float z = 0.0f;
	
	@Parameter(name = "rot", direction = ParameterDirection.IN)
	private float rot = 0.0f;
	
	@Parameter(name = "pitch", direction = ParameterDirection.IN)
	private float pitch = 0.0f;
	
	public MoveToPointOperationMode(BaseControlComponent<DroneService> component) {
		super(component);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
		for(int retry = 0; retry < NUM_RETRIES; retry++) {
			// #############################################################################
			// TODO we definitely need some sort of feedback (ret val, Exception, ...) here!
			DronePoint point = new DronePoint(x, y, z, rot, pitch);
			getService(DroneService.class).moveToPoint(point);
			// #############################################################################
			sleep(1000);
			
			//TODO: Improve this code
			if(getService(DroneService.class).getMissionState().equals(MissionState.PENDING) || 
			getService(DroneService.class).getMissionState().equals(MissionState.REJECTED))
			{
				executing = false;		
			}
			else //ACCEPTED, EXECUTING, ...
			{
				executing = true;
				break;
			}
			sleep(1500);
		}
	}

	@Override
	public void onCompleting() {
		super.onCompleting();
		sleep(1000);
	}

	@Override
	public void onStopping() {
		super.onStopping();
		sleep(1000);
	}
}
