package de.dfki.cos.basys.p4p.controlcomponent.lap;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.lap.service.LapService;

public abstract class BaseLapOperationMode extends BaseOperationMode<LapService>{
	
	private static final int MOCKUP_SERVICE_DURATION = 5000;
	
	protected int duration = 0;
	protected long startTime = 0;
	protected long endTime = 0;
	
	private String missionState;
	
	protected boolean executing = false;

	public BaseLapOperationMode(BaseControlComponent<LapService> component) {
		super(component);
	}

	@Override
	public void onResetting() {
		duration = 0;
		startTime = 0;
		endTime = 0;
		executing = false;
	}

	@Override
	public void onStarting() {
		startTime = System.currentTimeMillis();		
	}

	@Override
	public void onExecute() {
		try {
			while(executing) {
				missionState = getService(LapService.class).getMissionState();
				 
				switch (missionState) {
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
	}
	
	@Override
	protected void configureServiceMock(LapService serviceMock) {
		Mockito.doNothing().when(serviceMock).projectArrowsAndCircles(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyInt(), Mockito.anyList(), Mockito.anyInt(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyInt(), Mockito.anyInt());
		Mockito.doNothing().when(serviceMock).projectChar(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyInt(), Mockito.anyChar(), Mockito.anyDouble());
		Mockito.doNothing().when(serviceMock).projectCircle(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyInt(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble());
		Mockito.doNothing().when(serviceMock).projectEllipse(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyInt(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble());
		Mockito.doNothing().when(serviceMock).projectLine(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyInt(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble());
		Mockito.doNothing().when(serviceMock).projectMovingArrows(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyInt(), Mockito.anyList(), Mockito.anyInt(), Mockito.anyInt());
		Mockito.doNothing().when(serviceMock).projectMovingETA(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyInt(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble());
		Mockito.doNothing().when(serviceMock).projectPulsatingCircle(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyInt(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyInt());
		Mockito.doNothing().when(serviceMock).projectRectangle(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyInt(), Mockito.anyDouble(), Mockito.anyDouble());
		Mockito.doNothing().when(serviceMock).projectString(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyDouble());
		Mockito.doNothing().when(serviceMock).stopProjection();
		Mockito.doNothing().when(serviceMock).reset();
		Mockito.doNothing().when(serviceMock).abort();
		Mockito.doNothing().when(serviceMock).pause();
		Mockito.doNothing().when(serviceMock).resume();
		Mockito.when(serviceMock.getMissionState()).thenAnswer(new Answer<String>() {
			boolean accepted = false;
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				if(!accepted)
				{
					missionState = "accepted";
					accepted = true;
				}
				else { // accepted
					long elapsed = System.currentTimeMillis() - startTime;
					if (elapsed < MOCKUP_SERVICE_DURATION) {
						missionState = "executing";
					} else {
						missionState = "done";
					}
			}
				return missionState;
			}
			
		});
	}

}
