package de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.opmodes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.service.Notification;
import de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.service.SmartwatchService;

@OperationMode(name = "DisplayInfoMessage", shortName = "DIM", description = "display an info message for a human worker",
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.UNHOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP },
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE, ExecutionMode.AUTO })
public class DisplayInfoMessageOperationMode extends BaseSmartwatchOperationMode {

	@Parameter(name = "dim_message", direction = ParameterDirection.IN)
	private String message = "";


	public DisplayInfoMessageOperationMode(BaseControlComponent<SmartwatchService> component) {
		super(component);
	}

	@Override
	public void onStarting() {
		super.onStarting();

		// convert JSON string to Notification using Jackson
		Notification dim = null;
		try {
			dim = new ObjectMapper().readValue(message, new TypeReference<Notification>() {});
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		getService(SmartwatchService.class).displayInfoMessage(dim);

		sleep(1000);
	}

	@Override
	public void onExecute() {
		// Do nothing
		sleep(1000);
	}

	@Override
	public void onCompleting() {
		super.onCompleting();
		getService(SmartwatchService.class).reset();
		sleep(1000);
	}
}
