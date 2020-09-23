package de.dfki.cos.basys.p4p.wallettaskhandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.variable.value.JsonValue;
import org.camunda.bpm.engine.variable.value.ObjectValue;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.dfki.cos.basys.controlcomponent.ExecutionCommand;
import de.dfki.cos.basys.controlcomponent.ExecutionMode;
import de.dfki.cos.basys.controlcomponent.OccupationCommand;
import de.dfki.cos.basys.platform.runtime.component.model.ComponentRequest;
import de.dfki.cos.basys.platform.runtime.component.model.ComponentResponse;
import de.dfki.cos.basys.platform.runtime.component.model.ExecutionCommandRequest;
import de.dfki.cos.basys.platform.runtime.component.model.ExecutionModeRequest;
import de.dfki.cos.basys.platform.runtime.component.model.OccupationCommandRequest;
import de.dfki.cos.basys.platform.runtime.component.model.OperationModeRequest;
import de.dfki.cos.basys.platform.runtime.component.model.RequestStatus;
import de.dfki.cos.basys.platform.runtime.component.model.Variable;
import de.dfki.cos.basys.platform.runtime.component.model.VariableType;
import de.dfki.cos.basys.platform.runtime.processcontrol.ComponentRequestExecutionManager;
import de.dfki.cos.basys.platform.runtime.processcontrol.camunda.CamundaExternalTaskWorker;
import de.wallet.model.Lift;
import de.wallet.model.Plan;
import de.wallet.model.Request;
import de.wallet.model.custom.Command;
import de.wallet.model.custom.LiftFirstLevelCommand;
import de.wallet.model.custom.LiftGroundLevelCommand;
import de.wallet.model.custom.LiftSecondLevelCommand;
import de.wallet.model.custom.LiftThirdLevelCommand;
import de.wallet.model.custom.Utils;

/**
 * 
 * @author sokn01
 *
 */
public class ControlComponentWorker extends CamundaExternalTaskWorker {
	
	protected Plan plan;

	public ControlComponentWorker(Properties config) {
		super(config);
	}

	public ComponentRequest createComponentRequest(ExternalTask externalTask) {		
		ComponentRequest componentReq = null;
		Long level = null;
		
		ObjectValue planObjVal = externalTask.getVariableTyped("plan", true);
		plan = (Plan) planObjVal.getValue(Plan.class);
		
		Request request = plan.getRequests().get(0);
		Command command = null;
		if(request.getCommand().contains("LiftGroundLevelCommand")) {
			command = Utils.makeJsonToCommand(request.getCommand(), LiftGroundLevelCommand.class);
		} else if(request.getCommand().contains("LiftFirstLevelCommand")) {
			command = Utils.makeJsonToCommand(request.getCommand(), LiftFirstLevelCommand.class);
		} else if (request.getCommand().contains("LiftSecondLevelCommand")) {
			command = Utils.makeJsonToCommand(request.getCommand(), LiftSecondLevelCommand.class);		
		} else if (request.getCommand().contains("LiftThirdLevelCommand")) {
			command = Utils.makeJsonToCommand(request.getCommand(), LiftThirdLevelCommand.class);			
		}
		command.execute();
		if(command.getReceiver() instanceof Lift) {
			Lift receiver = (Lift)command.getReceiver();
			level = receiver.getLevel();
			plan.setResult(Utils.getJsonFormObject(receiver));
		}
			
		Variable levelVar = new Variable.Builder().name("level").value(level).type(VariableType.LONG).build();
		componentReq = new OperationModeRequest();
		OperationModeRequest req = (OperationModeRequest) componentReq;
		req.getInputParameters().add(levelVar);
		req.setOperationMode("LIFT");
		req.setComponentId("wallet-1");
		componentReq.setCorrelationId(externalTask.getId());
		componentReq.setOccupierId(externalTask.getProcessInstanceId());
		return componentReq;
	}
	
	@Override
	protected void doHandleComponentResponse(ComponentResponse response) {
		ExternalTask externalTask = externalTasks.remove(response.getRequest().getCorrelationId());
		if (response.getStatus() == RequestStatus.OK) {
			Map<String, Object> variables = new HashMap<>();
			variables.put("plan", plan);
			externalTaskService.complete(externalTask, variables);
		} else {
			externalTaskService.handleFailure(externalTask, response.getMessage(), "", maxRetryCount, retryTimeout);
		}
	}

}
