package de.dfki.cos.basys.p4p.controlcomponent.worker.opmodes;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.worker.service.NotificationService;
import de.dfki.iui.hrc.hybritcommand.HumanTaskDTO;

import java.util.UUID;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

@OperationMode(name = "RemoveObstacle", shortName = "REMOVE", description = "remove obstacle",  
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class RemoveObstacleOperationMode extends BaseWorkerOperationMode {

	public RemoveObstacleOperationMode(BaseControlComponent<NotificationService> component) {
		super(component);
	}
	
	@Override
	protected HumanTaskDTO getHumanTask() {
		return new HumanTaskDTO(UUID.randomUUID().toString(), "REMOVE", "Entferne Hindernis", "");
	}

	
}
