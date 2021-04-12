package de.dfki.cos.basys.p4p.controlcomponent.smartwatch;

import org.apache.thrift.TException;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.iui.hrc.hybritcommand.CommandState;
import de.dfki.iui.hrc.hybritcommand.HumanTaskDTO;

public abstract class BaseSmartwatchOperationMode extends BaseOperationMode<NotificationService> {

	protected long startTime = 0;
	protected long endTime = 0;
	protected int duration = 0;
	
	protected boolean executing = false;
	
	
	@Override
	protected void configureServiceMock(NotificationService serviceMock) {
		try {
			Mockito.doNothing().when(serviceMock).reconnect();
			Mockito.doNothing().when(serviceMock).reset();	
			Mockito.when(serviceMock.displayInfoMessage(Mockito.anyString())).thenReturn(CommandState.ACCEPTED);
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

	
	public BaseSmartwatchOperationMode(BaseControlComponent<NotificationService> component) {
		super(component);
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
