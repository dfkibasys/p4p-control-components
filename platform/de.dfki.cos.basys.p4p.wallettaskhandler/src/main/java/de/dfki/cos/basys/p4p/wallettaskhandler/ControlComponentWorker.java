package de.dfki.cos.basys.p4p.wallettaskhandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.engine.variable.value.ObjectValue;

import de.dfki.cos.basys.platform.runtime.component.model.ComponentRequest;
import de.dfki.cos.basys.platform.runtime.component.model.ComponentResponse;
import de.dfki.cos.basys.platform.runtime.component.model.OperationModeRequest;
import de.dfki.cos.basys.platform.runtime.component.model.RequestStatus;
import de.dfki.cos.basys.platform.runtime.component.model.Variable;
import de.dfki.cos.basys.platform.runtime.component.model.VariableType;
import de.dfki.cos.basys.platform.runtime.processcontrol.camunda.CamundaExternalTaskWorker;
import de.wallet.model.Plan;
import de.wallet.model.Request;
import de.wallet.model.custom.Command;
import de.wallet.model.custom.LiftFirstLevelCommand;
import de.wallet.model.custom.LiftGroundLevelCommand;
import de.wallet.model.custom.LiftHeightCommand;
import de.wallet.model.custom.LiftSecondLevelCommand;
import de.wallet.model.custom.LiftThirdLevelCommand;
import de.wallet.model.custom.Utils;
import de.wallet.model.Lift;

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
		Double liftHeight = null;
		
		ObjectValue planObjVal = externalTask.getVariableTyped("plan", false);
		String value = "{\"Plan\" : " + planObjVal.getValueSerialized() + "}";
		plan = (Plan) Utils.makeJsonToObject(value, Plan.class);
		
		Request request = plan.getRequests().get(0);
		Command command = null;
		if(request.getCommand().contains(LiftGroundLevelCommand.class.getSimpleName())) {
			command = Utils.makeJsonToCommand(request.getCommand(), LiftGroundLevelCommand.class);
		} else if(request.getCommand().contains(LiftFirstLevelCommand.class.getSimpleName())) {
			command = Utils.makeJsonToCommand(request.getCommand(), LiftFirstLevelCommand.class);
		} else if (request.getCommand().contains(LiftSecondLevelCommand.class.getSimpleName())) {
			command = Utils.makeJsonToCommand(request.getCommand(), LiftSecondLevelCommand.class);       
		} else if (request.getCommand().contains(LiftThirdLevelCommand.class.getSimpleName())) {
			command = Utils.makeJsonToCommand(request.getCommand(), LiftThirdLevelCommand.class);             
		} else if (request.getCommand().contains(LiftHeightCommand.class.getSimpleName())) {
			command = Utils.makeJsonToCommand(request.getCommand(), LiftHeightCommand.class);            
		}

		command.execute();
		if(command.getReceiver() instanceof Lift) {
			Lift receiver = (Lift)command.getReceiver();
			liftHeight = receiver.getHeight();
			plan.setResult(Utils.getJsonFormObject(receiver));
		}
		
		Variable levelVar = new Variable.Builder().name("height").value(liftHeight).type(VariableType.DOUBLE).build();
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
