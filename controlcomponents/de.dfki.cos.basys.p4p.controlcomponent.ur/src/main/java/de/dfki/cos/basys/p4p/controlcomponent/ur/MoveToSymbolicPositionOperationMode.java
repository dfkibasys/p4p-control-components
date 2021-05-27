package de.dfki.cos.basys.p4p.controlcomponent.ur;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.ur.service.URService;

@OperationMode(name = "MoveSymbolic", shortName = "MOVE_SYM", description = "moves component to a symbolic position", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class MoveToSymbolicPositionOperationMode extends BaseUROperationMode {

	@Parameter(name = "position", direction = ParameterDirection.IN)
	private String position = "";
	
	@Parameter(name = "duration", direction = ParameterDirection.OUT)
	private int duration_out = 0;
		
	public MoveToSymbolicPositionOperationMode(BaseControlComponent<URService> component) {
		super(component);
	}

	@Override
	public void onStarting() {		
		super.onStarting();
		getService(URService.class).moveToSymbolicPosition(position);
		sleep(1000);
	}


	@Override
	public void onCompleting() {
		super.onCompleting();
		duration_out = duration;
		sleep(1000);
	}

	@Override
	public void onStopping() {
		super.onStopping();
		sleep(1000);
	}
	
}
