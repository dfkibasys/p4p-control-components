package de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.opmodes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dfki.cos.basys.controlcomponent.annotation.Parameter;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.service.*;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.ParameterDirection;
import de.dfki.cos.basys.controlcomponent.annotation.OperationMode;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@OperationMode(name = "RequestTaskExecution", shortName = "RTE", description = "request execution of a task by a human worker",
		allowedCommands = {	ExecutionCommand.HOLD, ExecutionCommand.RESET, ExecutionCommand.START, ExecutionCommand.STOP }, 
		allowedModes = { ExecutionMode.PRODUCTION, ExecutionMode.SIMULATE })
public class RequestTaskExecutionOperationMode extends BaseSmartwatchOperationMode {

	private CountDownLatch counter;
	@Parameter(name = "task", direction = ParameterDirection.IN)
	private String task = "";
	

	public RequestTaskExecutionOperationMode(BaseControlComponent<SmartwatchService> component) {
		super(component);
	}

	@Override
	public void onStarting() {
		super.onStarting();

		counter = new CountDownLatch(1);

		TaskState.getInstance().addStateListener((oldState, newState) -> {
			if (newState.equals(SmartwatchStatus.TState.ACCEPTED) || newState.equals(SmartwatchStatus.TState.EXECUTING)) {
				executing = true;
				component.setErrorStatus(0, "OK");
				counter.countDown();
			}
			else if (newState.equals(SmartwatchStatus.TState.REJECTED)) {
				component.setErrorStatus(3, "rejected");
				counter.countDown();
			}
		});

		// precautionary set timeout error (gets overridden in case of success)
		component.setErrorStatus(4, "timeout");
		TaskRequest tr = null;
		try {
			tr = new ObjectMapper().readValue(task, new TypeReference<TaskRequest>() {});
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		getService(SmartwatchService.class).requestTaskExecution(tr);
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
		getService(SmartwatchService.class).reset();
	}

	@Override
	public void onStopping() {	
		super.onStopping();

		getService(SmartwatchService.class).reset();
	}
}
