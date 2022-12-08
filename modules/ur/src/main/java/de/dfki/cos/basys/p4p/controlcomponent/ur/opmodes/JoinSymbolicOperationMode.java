package de.dfki.cos.basys.p4p.controlcomponent.ur.opmodes;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.ur.service.UrService;

@OperationMode(name = "JoinSymbolic", shortName = "JOIN_SYM", description = "Join a picked object of specified type to a second object of specified type.",
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.UNHOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP },
		allowedModes = { ExecutionMode.SIMULATE })
public class JoinSymbolicOperationMode extends BaseUROperationMode {

	@Parameter(name = "object_type_a", direction = ParameterDirection.IN)
	private String object_type_a = "";
	@Parameter(name = "object_type_b", direction = ParameterDirection.IN)
	private String object_type_b = "";
	@Parameter(name = "duration", direction = ParameterDirection.OUT)
	private int duration_out = 0;

	public JoinSymbolicOperationMode(BaseControlComponent<UrService> component) {
		super(component);
	}

	@Override
	public void onStarting() {		
		super.onStarting();
		getService(UrService.class).joinSymbolic(object_type_a, object_type_b);
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
