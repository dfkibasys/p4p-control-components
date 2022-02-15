package de.dfki.cos.basys.p4p.controlcomponent.smartwatch.opmodes;

import java.util.UUID;

import de.dfki.cos.basys.p4p.controlcomponent.smartwatch.service.NotificationService;
import org.apache.thrift.TException;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.iui.hrc.hybritcommand.CommandState;
import de.dfki.iui.hrc.hybritcommand.HumanTaskDTO;

@OperationMode(name = "requestTaskExecution", shortName = "RTE", description = "this requests the execution of a task with a task description ", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class RequestTaskExecutionOperationMode extends BaseSmartwatchOperationMode {

	@Parameter(name = "description", direction = ParameterDirection.IN)
	private String taskDescription = "";
	
	@Parameter(name = "operationType", direction = ParameterDirection.IN)
	private String operationType = "";
	
	private HumanTaskDTO task = null;
	private String clientId = null;

	
	public RequestTaskExecutionOperationMode(BaseControlComponent<NotificationService> component) {
		super(component);
		clientId = UUID.randomUUID().toString();
	}

	@Override
	public void onResetting() {
		super.onResetting();
		taskDescription = "";
		operationType = "";
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
	
	private HumanTaskDTO getHumanTask() {
		return new HumanTaskDTO(UUID.randomUUID().toString(), operationType, taskDescription, clientId);
	}

}
