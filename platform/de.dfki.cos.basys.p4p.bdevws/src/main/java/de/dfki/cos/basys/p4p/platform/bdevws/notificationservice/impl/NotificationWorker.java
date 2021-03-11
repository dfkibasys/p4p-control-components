package de.dfki.cos.basys.p4p.platform.bdevws.notificationservice.impl;

import java.util.Properties;

import org.camunda.bpm.client.task.ExternalTask;
import de.dfki.cos.basys.platform.runtime.component.model.ComponentRequest;
import de.dfki.cos.basys.platform.runtime.component.model.ServiceRequest;
import de.dfki.cos.basys.platform.runtime.component.model.Variable;
import de.dfki.cos.basys.platform.runtime.component.model.VariableType;
import de.dfki.cos.basys.platform.runtime.processcontrol.camunda.CamundaExternalTaskWorker;

public class NotificationWorker extends CamundaExternalTaskWorker {
	
	public NotificationWorker(Properties config) {
		super(config);
	}

	public ComponentRequest createComponentRequest(ExternalTask externalTask) {		
		ServiceRequest req = new ServiceRequest();

		String componentId = externalTask.getVariable("componentId");
		if (componentId == null) {
			externalTaskService.handleFailure(externalTask, "No componentId", "ExternalTask does not contain a componentId", maxRetryCount, retryTimeout);
			return null;
		}
		
		
		req.setComponentId(componentId);	
		req.setServiceName("notify");		
		req.setCorrelationId(externalTask.getId());
		req.setOccupierId(externalTask.getProcessInstanceId());
		
		Variable timestamp = new Variable.Builder().name("timestamp").type(VariableType.DATE).value(externalTask.getVariable("timestamp")).build();
		Variable signalId = new Variable.Builder().name("signalId").type(VariableType.STRING).value(externalTask.getVariable("signalId")).build();
		Variable message = new Variable.Builder().name("message").type(VariableType.STRING).value(externalTask.getVariable("message")).build();
		Variable criticality = new Variable.Builder().name("criticality").type(VariableType.STRING).value(externalTask.getVariable("criticality")).build();
		Variable status = new Variable.Builder().name("status").type(VariableType.STRING).value(externalTask.getVariable("status")).build();
		Variable value = new Variable.Builder().name("value").type(VariableType.DOUBLE).value(externalTask.getVariable("value")).build();
		
		req.getInputParameters().add(timestamp);
		req.getInputParameters().add(signalId);
		req.getInputParameters().add(message);
		req.getInputParameters().add(criticality);
		req.getInputParameters().add(status);
		req.getInputParameters().add(value);
		
		return req;
	}


}
