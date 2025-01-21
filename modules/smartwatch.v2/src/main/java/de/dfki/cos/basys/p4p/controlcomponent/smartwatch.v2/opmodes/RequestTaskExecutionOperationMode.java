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

		// Register task state listener and wait for task to be queued (PENDING)
		TaskState.getInstance().addStateListener((oldState, newState) -> {
			if(newState.equals(oldState)) {
				LOG.info("Starting: no state change. Ignoring.");
				return;
			}

			// Task has been received and has been queued
			if(newState.equals(SmartwatchStatus.TState.PENDING)) {
				LOG.info("Starting PENDING");
				executing = true;
				component.setErrorStatus(0, "OK");
				counter.countDown();
			}
			else {
				LOG.warn("Received unexpected task state {}! Ignoring.", newState.toString());
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
	public void onExecute() {
		TaskState state;

		while(executing) {
			SmartwatchService service = getService(SmartwatchService.class);
			state = service.getTaskState();
			LOG.info("Current task state is {}.", state.getState().toString());
			switch(state.getState()) {
				// Task has been queued and waits for accept/reject
				case PENDING:
					component.setWorkState("Waiting for Worker to accept task ...");
					break;
				case EXECUTING:
					component.setWorkState("Task execution ongoing ...");
					break;
				case PAUSED:
					component.setWorkState("Task execution paused ...");
					break;
				case DONE:
					component.setWorkState("Task execution finished!");
					executing=false;
					break;
				case FAILED:
					executing=false;
					component.setWorkState("Task execution failed!");
					component.setErrorStatus(1, "failed");
					component.stop(component.getOccupierId());
					break;
				case CANCELLED:
					executing=false;
					component.setWorkState("Task execution cancelled by worker!");
					component.setErrorStatus(2, "cancelled");
					component.stop(component.getOccupierId());
					break;
				default:
					LOG.warn("Received unexpected task state {}!", state.getState().toString());
					break;

			}
			sleep(500);
		}
	}
}
