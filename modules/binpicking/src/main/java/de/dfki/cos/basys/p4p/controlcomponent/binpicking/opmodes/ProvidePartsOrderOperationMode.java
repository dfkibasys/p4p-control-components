package de.dfki.cos.basys.p4p.controlcomponent.binpicking.opmodes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.binpicking.service.BinPickingService;
import de.dfki.cos.basys.p4p.controlcomponent.binpicking.service.BinPickingStatus;
import de.dfki.cos.basys.p4p.controlcomponent.binpicking.service.MissionState;
import de.dfki.cos.basys.p4p.controlcomponent.binpicking.service.ProductPartRequest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@OperationMode(name = "ProvidePartsOrder", shortName = "PPO", description = "provide specific number of parts of a specific type at symbolic location ",
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class ProvidePartsOrderOperationMode extends BaseBinPickingOperationMode {

	private CountDownLatch counter;
	@Parameter(name = "bp_order", direction = ParameterDirection.IN)
	private String order = "";


	public ProvidePartsOrderOperationMode(BaseControlComponent<BinPickingService> component) {
		super(component);
	}

	@Override
	public void onStarting() {
		super.onStarting();

		counter = new CountDownLatch(1);

		// convert JSON string to List<ProductPartRequest> using Jackson
		List<ProductPartRequest> ppr = null;
		try {
			ppr = new ObjectMapper().readValue(order, new TypeReference<List<ProductPartRequest>>() {});
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		// start live image listeners
		MissionState.getInstance().addStateListener((oldState, newState) -> {
			if (newState.equals(BinPickingStatus.MState.ACCEPTED)) {
				component.setErrorStatus(0, "OK");
				counter.countDown();
			}
			else if (newState.equals(BinPickingStatus.MState.REJECTED)) {
				component.setErrorStatus(3, "rejected");
				counter.countDown();
			}
		});

		// precautionary set timeout error (gets overridden in case of success)
		component.setErrorStatus(4, "timeout");

		// Start Sorting and providing of specified part types in specified number at specified symbolic target location
		getService(BinPickingService.class).provideParts(ppr);
		sleep(1000);

		try {
			counter.await(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onCompleting() {
		super.onCompleting();

		sleep(1000);

		getService(BinPickingService.class).reset();
		
	}

	@Override
	public void onStopping() {	
		super.onStopping();
	}
}
