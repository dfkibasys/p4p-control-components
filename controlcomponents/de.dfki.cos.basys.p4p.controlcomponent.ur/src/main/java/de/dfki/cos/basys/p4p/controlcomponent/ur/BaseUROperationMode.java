package de.dfki.cos.basys.p4p.controlcomponent.ur;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.ur.service.URService;
import edu.wpi.rail.jrosbridge.Goal.GoalStatusEnum;

public class BaseUROperationMode extends BaseOperationMode<URService>{
	private static final int MOCKUP_SERVICE_DURATION = 5000;
	
	protected long startTime = 0;
	protected long endTime = 0;
	protected int duration = 0;
	
	public BaseUROperationMode(BaseControlComponent<URService> component) {
		super(component);
	}

	@Override
	public void onResetting() {
		duration = 0;
		startTime = 0;
		endTime = 0;
		getService(URService.class).reset();
	}

	@Override
	public void onStarting() {
		startTime = System.currentTimeMillis();				
	}

	@Override
	public void onExecute() {
		boolean executing = true;
		while(executing) {
			GoalStatusEnum status = getService(URService.class).getStatus();
			LOGGER.debug("Status : " + status);
			 
			switch (status) {
			case ABORTED:
				component.setErrorStatus(4, "ABORTED");
				executing = false;
				break;
			case ACTIVE:
				break;
			case LOST:
				component.setErrorStatus(9, "LOST");
				executing = false;
				break;
			case PENDING:
				break;
			case PREEMPTED:
				component.setErrorStatus(2, "PREEMPTED");	
				executing = false;			
				break;
			case PREEMPTING:
				break;
			case RECALLED:
				component.setErrorStatus(8, "RECALLED");
				executing = false;
				break;
			case RECALLING:
				break;
			case REJECTED:
				component.setErrorStatus(5, "REJECTED");
				executing = false;
				break;
			case SUCCEEDED:
				executing = false;
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
	protected void configureServiceMock(URService serviceMock) {
		Mockito.doNothing().when(serviceMock).reset();		
		Mockito.doNothing().when(serviceMock).moveToSymbolicPosition(Mockito.anyString());
		Mockito.when(serviceMock.getStatus()).thenAnswer(new Answer<GoalStatusEnum>() {

			@Override
			public GoalStatusEnum answer(InvocationOnMock invocation) throws Throwable {
				long elapsed = System.currentTimeMillis() - startTime;
				GoalStatusEnum  result;
				if (elapsed < MOCKUP_SERVICE_DURATION) {
					result = GoalStatusEnum.PENDING;
				} else {
					result = GoalStatusEnum.SUCCEEDED;
				}
				return result;
			}
			
		});
	
	}
}
