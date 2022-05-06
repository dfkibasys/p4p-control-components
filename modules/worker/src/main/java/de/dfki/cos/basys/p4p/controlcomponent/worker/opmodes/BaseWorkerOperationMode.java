package de.dfki.cos.basys.p4p.controlcomponent.worker.opmodes;

import de.dfki.cos.basys.p4p.controlcomponent.worker.service.NotificationService;
import org.apache.thrift.TException;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.iui.hrc.hybritcommand.CommandState;
import de.dfki.iui.hrc.hybritcommand.HumanTaskDTO;

public abstract class BaseWorkerOperationMode extends BaseOperationMode<NotificationService> {

	protected long startTime = 0;
	protected long endTime = 0;
	protected int duration = 0;
	//private String clientId = null;
	
	private boolean executing = false;
	
	
	@Override
	protected void configureServiceMock(NotificationService serviceMock) {
		try {
			Mockito.doNothing().when(serviceMock).reconnect();
			Mockito.doNothing().when(serviceMock).reset();			
			Mockito.when(serviceMock.requestTaskExecution(Mockito.any(HumanTaskDTO.class))).thenReturn(CommandState.ACCEPTED);
			Mockito.when(serviceMock.getCommandState(Mockito.anyString())).thenAnswer(new Answer<CommandState>() {

				@Override
				public CommandState answer(InvocationOnMock arg0) throws Throwable {
					long elapsed = System.currentTimeMillis() - startTime;
					return (elapsed < 5000) ? CommandState.EXECUTING: CommandState.FINISHED;
				}
			});
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public BaseWorkerOperationMode(BaseControlComponent<NotificationService> component) {
		super(component);
		//clientId = UUID.randomUUID().toString();
	}


	@Override
	public void onResetting() {
		duration = 0;
		startTime = 0;
		endTime = 0;
		getService(NotificationService.class).reset();
		executing = false;
	}

	@Override
	public void onStarting() {

		HumanTaskDTO task = getHumanTask();
		
		int retryCount = 20;
		int i=0;
		startTime = System.currentTimeMillis();	
		
		while (!executing && i < retryCount) {		
			try {
				CommandState state = getService(NotificationService.class).requestTaskExecution(task);
				if(state==CommandState.ACCEPTED)
					executing = true;
				else if(state==CommandState.REJECTED) {
					LOGGER.warn(" Notification ressource busy. Retrying (#{}/{}) ...", i, retryCount);
					i++;
				}
				else  {
					LOGGER.error(" Received unexpected command state {}! Aborting ... ", state);
					component.setErrorStatus(-1, "Unexpected command state " + state);
					executing = false;
				}
			} catch (TException e1) {
				i++;
				//e1.printStackTrace();
				LOGGER.warn(" Notification ressource not responding. Retrying (#{}/{}) ...", i, retryCount);			
				getService(NotificationService.class).reconnect();
				sleep(1000);
			}
		}
		

	}
	
	protected abstract HumanTaskDTO getHumanTask();

	@Override
	public void onExecute() {
		
		CommandState state = CommandState.EXECUTING;
		
		while(executing) {
			try {
				state = getService(NotificationService.class).getCommandState("");
			} catch (TException e) {
				LOGGER.warn(" Notification ressource not responding. Retrying ...");
				sleep(1500);
				getService(NotificationService.class).reconnect();
				continue;
			}
			
			switch(state) {
			case EXECUTING:
				// Wait for completion
				break;
			case ABORTED:
				component.setErrorStatus(1, "aborted");
				component.stop(component.getOccupierId());
				executing = false;
				break;
			case FINISHED:
				executing = false;
				break;
			case READY:
				executing = false;
				break;
			case PAUSED:
				// TODO should the component be hold here?
				break;
			default:
				LOGGER.warn("Received unexpected CommandState {} during task execution!", state);
				break;
			}
			sleep(500);
		}

	}

	@Override
	public void onCompleting() {
		endTime = System.currentTimeMillis();
		duration = (int) (endTime - startTime);	
		executing = false;
	}

	@Override
	public void onStopping() {
		endTime = System.currentTimeMillis();
		duration = (int) (endTime - startTime);	
		executing = false;
	}


}
