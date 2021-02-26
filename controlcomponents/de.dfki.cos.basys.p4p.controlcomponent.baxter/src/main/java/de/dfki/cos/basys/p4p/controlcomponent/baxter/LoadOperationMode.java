package de.dfki.cos.basys.p4p.controlcomponent.baxter;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.baxter.service.BaxterService;

@OperationMode(name = "Load", shortName = "LOAD", description = "Load something", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class LoadOperationMode extends BaseBaxterOperationMode{

	public LoadOperationMode(BaseControlComponent<BaxterService> component) {
		super(component);
	}

	@Override
	public void onResetting() {
		super.onResetting();
		sleep(1000);
	}

	@Override
	public void onStarting() {	
		super.onStarting();
		sleep(1000);
	}

	@Override
	public void onCompleting() {
		super.onCompleting();
		sleep(1000);
	}

	@Override
	public void onStopping() {
		super.onStopping();
		sleep(1000);
	}

}
