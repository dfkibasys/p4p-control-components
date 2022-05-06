package de.dfki.cos.basys.p4p.controlcomponent.mir.opmodes;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;

import java.sql.Date;
import java.util.concurrent.TimeUnit;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.dfki.cos.basys.common.rest.mir.MiRState;
import de.dfki.cos.basys.common.rest.mir.MirService;
import de.dfki.cos.basys.common.rest.mir.dto.MissionInstanceInfo;
import de.dfki.cos.basys.common.rest.mir.dto.Status;
import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "MoveSymbolic", shortName = "MVSYM", description = "moves MiR to a symbolic position", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class MoveToSymbolicPositionOperationMode extends BaseOperationMode<MirService> {

	@Parameter(name = "position", direction = ParameterDirection.IN)
	private String position = "Station-Wait";
	
	@Parameter(name = "duration", direction = ParameterDirection.OUT)
	private int duration = 0;
	
	private MissionInstanceInfo currentMission = null;
	
	private long startTime = 0;
	private long endTime = 0;
		
	public MoveToSymbolicPositionOperationMode(BaseControlComponent<MirService> component) {
		super(component);
	}

	@Override
	public void onResetting() {
		duration = 0;
		startTime = 0;
		endTime = 0;
		currentMission = null;
		try {			
			Status status = getService(MirService.class).setRobotStatus(MiRState.READY);	
			//TODO check status
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			component.setErrorStatus(3, e.getMessage());
			component.stop(component.getOccupierId());
		}
	}

	@Override
	public void onStarting() {		
		startTime = System.currentTimeMillis();	
		try {
			currentMission = getService(MirService.class).gotoSymbolicPosition(position);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			component.setErrorStatus(3, e.getMessage());
			component.stop(component.getOccupierId());
		}
	}

	@Override
	public void onExecute() {
		try {
			boolean executing = true;
			while(executing) {
				currentMission = getService(MirService.class).getMissionInstanceInfo(currentMission.id);
				LOGGER.debug("MissionState is " + currentMission.state);
				 
				switch (currentMission.state.toLowerCase()) {
				case "pending":
					break;
				case "executing":
					break;
				case "done":
					executing=false;
					break;
				case "failed":
					executing=false;
					component.setErrorStatus(1, "failed");
					component.stop(component.getOccupierId());
					break;
				case "aborted":
					executing=false;
					component.setErrorStatus(2, "aborted");
					component.stop(component.getOccupierId());
					break;
				default:
					break;
				}
	
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			component.setErrorStatus(3, e.getMessage());
			component.stop(component.getOccupierId());
		}
	}

	@Override
	public void onCompleting() {
		endTime = System.currentTimeMillis();
		duration = (int) (endTime - startTime);
	}

	@Override
	public void onStopping() {
		endTime = System.currentTimeMillis();
		duration = (int) (endTime - startTime);
		try {
			Status status = getService(MirService.class).setRobotStatus(MiRState.PAUSED);
			//TODO: check status
			if (currentMission != null) {
				getService(MirService.class).dequeueMissionInstance(currentMission.id);
				currentMission = null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
		}	
	}

	@Override
	protected void configureServiceMock(MirService serviceMock) {
		Mockito.when(serviceMock.setRobotStatus(MiRState.PAUSED)).thenReturn(new Status());
		Mockito.when(serviceMock.dequeueMissionInstance(Mockito.anyInt())).thenReturn(true);
		Mockito.when(serviceMock.gotoSymbolicPosition(Mockito.anyString())).thenReturn(new MissionInstanceInfo());
		Mockito.when(serviceMock.gotoSymbolicPosition(null)).thenReturn(new MissionInstanceInfo());
		Mockito.when(serviceMock.getMissionInstanceInfo(Mockito.anyInt())).thenAnswer(new Answer<MissionInstanceInfo>() {

			@Override
			public MissionInstanceInfo answer(InvocationOnMock invocation) throws Throwable {
				long elapsed = System.currentTimeMillis() - startTime;
				MissionInstanceInfo result = new MissionInstanceInfo();
				if (elapsed < 10000) {
					result.state = "executing";
				} else {
					result.state = "done";
				}
				return result;
			}

	    }); 
	}
}
