package de.dfki.cos.basys.p4p.controlcomponent.smartwatch;

import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.controlcomponent.impl.BaseOperationMode;
import de.dfki.iui.hrc.hybritcommand.CommandState;
import de.dfki.iui.hrc.hybritcommand.HumanTaskDTO;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.TException;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "requestTaskExecution", shortName = "RTE", description = "this requests the execution of a task with a task description ", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATION })
public class RequestTaskExecutionOperationMode extends BaseOperationMode<NotificationService> {

	@Parameter(name = "description", direction = ParameterDirection.IN)
	private String taskDescription = "";
	
	@Parameter(name = "operationType", direction = ParameterDirection.IN)
	private String operationType = "";
	
	private long startTime = 0;
	
	
	private NotificationService service;
	private HumanTaskDTO task = null;
	private String clientId = null;
	
	@Override
	protected void configureServiceMock(NotificationService serviceMock) {
		try {
			Mockito.when(service.requestTaskExecution(task)).thenReturn(CommandState.ACCEPTED);
			Mockito.when(service.getCommandState(task.businessKey)).thenAnswer(new Answer<CommandState>() {

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

	
	public RequestTaskExecutionOperationMode(BaseControlComponent component) {
		super(component);
		service = getService(NotificationService.class);
		clientId = UUID.randomUUID().toString();
	}


	@Override
	public void onResetting() {
		startTime = 0;
		task = null;	
		taskDescription = "";
		operationType = "";
	}

	@Override
	public void onStarting() {
		boolean taskExecRequested = false;
		CommandState state = CommandState.ABORTED;
		
		task = readParams();
		
		while(!taskExecRequested) {
			try {
				state = service.requestTaskExecution(task);
				taskExecRequested = true;
			} catch (TException e) {
				LOGGER.warn(" Notification ressource not responding. Retrying ...");
				sleep(1500);
				service.reconnect();
			}
		}
	}
	
	private HumanTaskDTO readParams() {
		return new HumanTaskDTO(UUID.randomUUID().toString(), operationType, taskDescription, clientId);
	}

	@Override
	public void onExecute() {
		boolean executing = true;
		CommandState state = CommandState.EXECUTING;
		
		while(executing) {
			try {
				state = service.getCommandState("");
			} catch (TException e) {
				LOGGER.warn(" Notification ressource not responding. Retrying ...");
				sleep(1500);
				service.reconnect();
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
	}

	@Override
	public void onStopping() {
	}

	public void sleep(long ms) {
		try {
			TimeUnit.MILLISECONDS.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public long NowMS() {
		return System.currentTimeMillis();
	}
}
