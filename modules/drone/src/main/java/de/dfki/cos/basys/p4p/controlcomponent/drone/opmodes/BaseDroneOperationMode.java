package de.dfki.cos.basys.p4p.controlcomponent.drone.opmodes;

import java.util.Collections;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneService;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.DroneStatus.MState;
import de.dfki.cos.basys.p4p.controlcomponent.drone.service.MissionState;
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
			switch(state.getState()) {
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
		Mockito.when(serviceMock.detectObstacles(Mockito.any())).thenReturn(Collections.emptyList());
		Mockito.when(serviceMock.getWorkState()).thenReturn(WorkState.getInstance());
		Mockito.doNothing().when(serviceMock).reset();

		Mockito.doAnswer((Answer<Void>) invocationOnMock -> {
			MissionState.getInstance().setState(MState.EXECUTING);
			return null;
		}).when(serviceMock).takeOff();

		Mockito.doAnswer((Answer<Void>) invocationOnMock -> {
			MissionState.getInstance().setState(MState.EXECUTING);
			return null;
		}).when(serviceMock).startLiveImage();

		Mockito.doAnswer((Answer<Void>) invocationOnMock -> {
			MissionState.getInstance().setState(MState.EXECUTING);
			return null;
		}).when(serviceMock).stopLiveImage();

		Mockito.doAnswer((Answer<Void>) invocationOnMock -> {
			MissionState.getInstance().setState(MState.EXECUTING);
			return null;
		}).when(serviceMock).startRTMPStream();

		Mockito.doAnswer((Answer<Void>) invocationOnMock -> {
			MissionState.getInstance().setState(MState.EXECUTING);
			return null;
		}).when(serviceMock).stopRTMPStream();

		Mockito.doAnswer((Answer<Void>) invocationOnMock -> {
			MissionState.getInstance().setState(MState.EXECUTING);
			return null;
		}).when(serviceMock).land();

		Mockito.doAnswer((Answer<Void>) invocationOnMock -> {
			MissionState.getInstance().setState(MState.EXECUTING);
			return null;
		}).when(serviceMock).moveToSymbolicPosition(Mockito.anyString());

		Mockito.doAnswer((Answer<Void>) invocationOnMock -> {
			MissionState.getInstance().setState(MState.EXECUTING);
			return null;
		}).when(serviceMock).moveToPoint(Mockito.any());

		Mockito.doAnswer((Answer<Void>) invocationOnMock -> {
			MissionState.getInstance().setState(MState.EXECUTING);
			return null;
		}).when(serviceMock).moveToWaypoints(Mockito.any());

		Mockito.when(serviceMock.getMissionState()).thenAnswer(new Answer<MissionState>() {
			boolean accepted = false;
			@Override
			public MissionState answer(InvocationOnMock invocation) {
				if(!accepted)
				{
					MissionState.getInstance().setState(MState.ACCEPTED);
					accepted = true;
				}
				else { // accepted
					long elapsed = System.currentTimeMillis() - startTime;
					if (elapsed < MOCKUP_SERVICE_DURATION) {
						MissionState.getInstance().setState(MState.EXECUTING);
					} else {
						MissionState.getInstance().setState(MState.DONE);
					}
			}
				return MissionState.getInstance();
			}
			
		});
	
	}
}
