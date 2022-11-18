package de.dfki.cos.basys.p4p.controlcomponent.ur.opmodes;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.ur.service.UrService;

@OperationMode(name = "PPSymbolic", shortName = "PP_SYM", description = "Pick and place an object of specified type from a symbolic " +
		"source location to a symbolic target location.",
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.UNHOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP },
		allowedModes = { ExecutionMode.SIMULATE })
public class PickAndPlaceSymbolicOperationMode extends BaseUROperationMode {

	@Parameter(name = "source_location", direction = ParameterDirection.IN)
	private String source_location = "";
	@Parameter(name = "object_type", direction = ParameterDirection.IN)
	private String object_type = "";
	@Parameter(name = "target_location", direction = ParameterDirection.IN)
	private String target_location = "";

	@Parameter(name = "duration", direction = ParameterDirection.OUT)
	private int duration_out = 0;

	public PickAndPlaceSymbolicOperationMode(BaseControlComponent<UrService> component) {
		super(component);
	}

	@Override
	public void onStarting() {		
		super.onStarting();
		getService(UrService.class).pickAndPlaceSymbolic(object_type, source_location, target_location);
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
