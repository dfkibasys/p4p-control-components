package de.dfki.cos.basys.p4p.controlcomponent.worker.opmodes;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.worker.service.NotificationService;
import de.dfki.iui.hrc.hybritcommand.HumanTaskDTO;

import java.util.UUID;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "Load", shortName = "LOAD", description = "Load something", 
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class LoadOperationMode extends BaseWorkerOperationMode {
		
	public LoadOperationMode(BaseControlComponent<NotificationService> component) {
		super(component);
	}

	@Override
	protected HumanTaskDTO getHumanTask() {
		return new HumanTaskDTO(UUID.randomUUID().toString(), "LOAD", "Belade ...", "");
	}

}
