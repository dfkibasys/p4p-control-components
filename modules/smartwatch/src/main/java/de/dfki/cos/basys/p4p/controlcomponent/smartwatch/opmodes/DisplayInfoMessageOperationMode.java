package de.dfki.cos.basys.p4p.controlcomponent.smartwatch.opmodes;

import de.dfki.cos.basys.p4p.controlcomponent.smartwatch.service.NotificationService;
import org.apache.thrift.TException;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.iui.hrc.hybritcommand.CommandState;

@OperationMode(name = "displayInfoMessage", shortName = "DIM", description = "mode for displaying a string based message on the display of the smartwatch", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class DisplayInfoMessageOperationMode extends BaseSmartwatchOperationMode {

	@Parameter(name = "message", direction = ParameterDirection.IN)
	private String message = "";
	
	public DisplayInfoMessageOperationMode(BaseControlComponent<NotificationService> component) {
		super(component);
	}


	@Override
	public void onResetting() {
		super.onResetting();
		message = "";	
	}

	@Override
	public void onStarting() {
		int retryCount = 20;
		int i=0;
		startTime = System.currentTimeMillis();	
		
		while (!executing && i < retryCount) {		
			try {
				CommandState state = getService(NotificationService.class).displayInfoMessage(message);
				if(state==CommandState.ACCEPTED)
					executing = false; // ugly
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
}
