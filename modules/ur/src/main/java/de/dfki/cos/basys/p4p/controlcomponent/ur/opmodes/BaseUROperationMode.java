package de.dfki.cos.basys.p4p.controlcomponent.ur.opmodes;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.ur.service.UrService;
import de.dfki.cos.basys.p4p.controlcomponent.ur.service.URState.MissionState;

public class BaseUROperationMode extends BaseOperationMode<UrService>{
	private static final int MOCKUP_SERVICE_DURATION = 5000;
	
	protected long startTime = 0;
	protected long endTime = 0;
	protected int duration = 0;
	
	public BaseUROperationMode(BaseControlComponent<UrService> component) {
		super(component);
	}

	@Override
	public void onResetting() {
		duration = 0;
		startTime = 0;
		endTime = 0;
		getService(UrService.class).reset();
	}

	@Override
	public void onStarting() {
		startTime = System.currentTimeMillis();				
	}

	@Override
	public void onExecute() {
		boolean executing = true;
		while(executing) {
			MissionState state = getService(UrService.class).getMissionState();
			LOGGER.debug("Status : " + state);
			 
			switch (state) {		
			case CANCELLED:
				component.setErrorStatus(4, "CANCELLED");
				executing = false;
				break;
			case DONE:
				executing = false;
				break;
			case EXECUTING:
				break;				
			case FAILED:
				component.setErrorStatus(2, "FAILED");
				executing = false;
				break;
			case PENDING:
				break;				
			default:
				LOGGER.error("Received unexpected mission state {}", state.toString());
				component.setErrorStatus(1, "UNEXPECTED STATE");
				break;
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (component.getErrorCode() > 0) {
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
	}
	
	
	@Override
	protected void configureServiceMock(UrService serviceMock) {
		Mockito.doNothing().when(serviceMock).reset();		
		Mockito.doNothing().when(serviceMock).moveToSymbolicPosition(Mockito.anyString());
		Mockito.when(serviceMock.getMissionState()).thenAnswer(new Answer<MissionState>() {

			@Override
			public MissionState answer(InvocationOnMock invocation) throws Throwable {
				long elapsed = System.currentTimeMillis() - startTime;
				MissionState  result;
				if (elapsed < MOCKUP_SERVICE_DURATION) {
					result = MissionState.EXECUTING;
				} else {
					result = MissionState.DONE;
				}
				return result;
			}
			
		});
	
	}
}
