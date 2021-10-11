package de.dfki.cos.basys.p4p.platform.bdevws.incidentservice.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.p4p.platform.bdevws.incidentservice.IncidentTreatmentService;
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

public class IncidentTreatmentServiceComponent extends BasysComponent<IncidentTreatmentService> {

	public IncidentTreatmentServiceComponent(Properties config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	public IncidentTreatmentServiceComponent(Properties config, ServiceProvider<IncidentTreatmentService> serviceProvider) {
		super(config, serviceProvider);
		// TODO Auto-generated constructor stub
	}

	public IncidentTreatmentServiceComponent(Properties config,
			Supplier<ServiceProvider<IncidentTreatmentService>> serviceProviderSupplier) {
		super(config, serviceProviderSupplier);
		// TODO Auto-generated constructor stub
	}

	
}
