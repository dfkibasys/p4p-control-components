package de.dfki.cos.basys.p4p.controlcomponent.lap;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.lap.service.LapService;

public class BaseLapOperationMode extends BaseOperationMode<LapService>{
	
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
		//TODO
	}

}
