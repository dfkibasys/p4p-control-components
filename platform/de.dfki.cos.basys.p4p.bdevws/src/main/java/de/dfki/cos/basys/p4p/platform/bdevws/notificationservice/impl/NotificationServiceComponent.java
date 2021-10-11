package de.dfki.cos.basys.p4p.platform.bdevws.notificationservice.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.platform.runtime.component.BasysComponent;
import de.dfki.cos.basys.platform.runtime.component.model.ComponentRequestStatus;
import de.dfki.cos.basys.platform.runtime.component.model.RequestStatus;
import de.dfki.cos.basys.platform.runtime.component.model.ServiceRequest;
import de.dfki.cos.basys.platform.runtime.component.model.Variable;
import de.eyeled.basyx.bdevws.notifications.entities.BDEVWSNotification;
import de.eyeled.basyx.bdevws.notifications.entities.enums.BDEVWSNotificationCriticality;
import de.eyeled.basyx.bdevws.notifications.entities.enums.BDEVWSNotificationRole;
import de.eyeled.basyx.bdevws.notifications.entities.enums.BDEVWSNotificationSkill;
import de.eyeled.basyx.bdevws.notifications.entities.enums.BDEVWSNotificationStatus;
import de.eyeled.basyx.bdevws.notifications.service.interfaces.NotificationService;

public class NotificationServiceComponent extends BasysComponent<NotificationService> {

	public NotificationServiceComponent(Properties config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	public NotificationServiceComponent(Properties config, ServiceProvider<NotificationService> serviceProvider) {
		super(config, serviceProvider);
		// TODO Auto-generated constructor stub
	}

	public NotificationServiceComponent(Properties config,
			Supplier<ServiceProvider<NotificationService>> serviceProviderSupplier) {
		super(config, serviceProviderSupplier);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ComponentRequestStatus handleServiceRequest(ServiceRequest req) {
		if ("notify".equals(req.getServiceName())) {
			return doNotify(req);
		} else { 
			// reject service request
			return super.handleServiceRequest(req);
		}
	}
	
	
	private ComponentRequestStatus doNotify(ServiceRequest request) {

		Variable messageVar = request.getInputParameters().stream().filter(v -> v.getName().equalsIgnoreCase("message")).findFirst().orElse(null);
		Variable signalIdVar = request.getInputParameters().stream().filter(v -> v.getName().equalsIgnoreCase("signalId")).findFirst().orElse(null);
		Variable criticalityVar = request.getInputParameters().stream().filter(v -> v.getName().equalsIgnoreCase("criticality")).findFirst().orElse(null);
		Variable statusVar = request.getInputParameters().stream().filter(v -> v.getName().equalsIgnoreCase("status")).findFirst().orElse(null);		
		Variable timestampVar = request.getInputParameters().stream().filter(v -> v.getName().equalsIgnoreCase("timestamp")).findFirst().orElse(null);
		Variable valueVar = request.getInputParameters().stream().filter(v -> v.getName().equalsIgnoreCase("value")).findFirst().orElse(null);
		
		String topic = "";
		
		BDEVWSNotificationCriticality c = Enum.valueOf(BDEVWSNotificationCriticality.class, criticalityVar.getValue().toString());
		BDEVWSNotificationStatus      s = Enum.valueOf(BDEVWSNotificationStatus.class, statusVar.getValue().toString());
		
		Instant i = Instant.now();
		
		/*
		public BDEVWSNotification(String message, String signalId, double value, long timestamp,
				BDEVWSNotificationCriticality criticality, BDEVWSNotificationStatus status,
				String userId, String deviceId,String confirmedBy, long confirmedDate,
				List<BDEVWSNotificationRole> roles,List<BDEVWSNotificationSkill> skills) {
		*/
		
		BDEVWSNotification notification = new BDEVWSNotification(
				messageVar.getValue().toString(), 
				signalIdVar.getValue().toString(), 
				topic,
				Double.parseDouble(valueVar.getValue().toString()), 
				i.toEpochMilli(), 
				c, 
				s, 
				null, null, null, 0, 
				Collections.singletonList(BDEVWSNotificationRole.ALL),
				Collections.singletonList(BDEVWSNotificationSkill.UNDEFINED));
		
		getService().notify(notification);
		//TODO: replace NOOP with ACCEPTED and send ComponentResponse later async 
		ComponentRequestStatus status =  new ComponentRequestStatus.Builder().status(RequestStatus.NOOP).message("sent notification").build();
		return status;
	}

	
	
}
