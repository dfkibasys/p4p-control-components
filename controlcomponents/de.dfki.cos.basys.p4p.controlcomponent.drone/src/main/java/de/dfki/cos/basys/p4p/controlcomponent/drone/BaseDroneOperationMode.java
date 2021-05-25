package de.dfki.cos.basys.p4p.controlcomponent.drone;

import java.util.Collections;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneService;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneStatus.MissionState;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.WorkState;

public abstract class BaseDroneOperationMode extends BaseOperationMode<DroneService> {
	private static final Logger LOG = LoggerFactory.getLogger(BaseDroneOperationMode.class);
	private static final int MOCKUP_SERVICE_DURATION = 5000;
	
	protected long startTime = 0;
	protected long endTime = 0;
	protected int duration = 0;
	
	protected boolean executing = false;
	
	public BaseDroneOperationMode(BaseControlComponent<DroneService> component) {
		super(component);
	}

	@Override
	public void onResetting() {
		duration = 0;
		startTime = 0;
		endTime = 0;
		getService(DroneService.class).reset();
		executing = false;
	}
	
	
	@Override
	public void onStarting() {
		startTime = System.currentTimeMillis();		
	}

	@Override
	public void onExecute() {
		MissionState state;
		while(executing) {
			DroneService service = getService(DroneService.class);
			state = service.getMissionState();
			component.setWorkState(service.getWorkState().toString());
			LOG.debug("Current mission state is {}.", state);
			switch(state) {
				case PENDING:
					break;
				case EXECUTING:
					break;
				case DONE:
					executing=false;
					break;
				case FAILED:
					executing=false;
					component.setErrorStatus(1, "failed");
					component.stop(component.getOccupierId());
					break;
				case ABORTED:
					executing=false;
					component.setErrorStatus(2, "aborted");
					component.stop(component.getOccupierId());
					break;
			default:
				LOG.warn("Received unexpected mission state {}!", state);
				break;

			}
			sleep(500);
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
	}
	
	@Override
	protected void configureServiceMock(DroneService serviceMock) {
		Mockito.when(serviceMock.detectObstacles(Mockito.anyString())).thenReturn(Collections.emptyList());
		Mockito.when(serviceMock.getWorkState()).thenReturn(WorkState.getInstance());
		Mockito.doNothing().when(serviceMock).takeOff();
		Mockito.doNothing().when(serviceMock).reset();
		Mockito.doNothing().when(serviceMock).startLiveImage();
		Mockito.doNothing().when(serviceMock).stopLiveImage();
		Mockito.doNothing().when(serviceMock).startRTMPStream();
		Mockito.doNothing().when(serviceMock).stopRTMPStream();
		Mockito.doNothing().when(serviceMock).land();
		Mockito.doNothing().when(serviceMock).moveToSymbolicPosition(Mockito.anyString());
		Mockito.doNothing().when(serviceMock).moveToPoint(Mockito.any());
		Mockito.when(serviceMock.getMissionState()).thenAnswer(new Answer<MissionState>() {
			boolean accepted = false;
			@Override
			public MissionState answer(InvocationOnMock invocation) throws Throwable {
				MissionState  result;
				if(!accepted)
				{
					result = MissionState.ACCEPTED;
					accepted = true;
				}
				else { // accepted
					long elapsed = System.currentTimeMillis() - startTime;
					if (elapsed < MOCKUP_SERVICE_DURATION) {
						result = MissionState.EXECUTING;
					} else {
						result = MissionState.DONE;
					}
			}
				return result;
			}
			
		});
	
	}
}
