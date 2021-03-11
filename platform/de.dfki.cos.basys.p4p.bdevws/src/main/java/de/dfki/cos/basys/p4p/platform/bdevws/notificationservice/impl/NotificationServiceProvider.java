package de.dfki.cos.basys.p4p.platform.bdevws.notificationservice.impl;

import java.util.Properties;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.eyeled.basyx.bdevws.notifications.entities.BDEVWSNotification;
import de.eyeled.basyx.bdevws.notifications.service.BdeVwsNotificationService;
import de.eyeled.basyx.bdevws.notifications.service.interfaces.NotificationService;

public class NotificationServiceProvider implements NotificationService, ServiceProvider<NotificationService> {

	private NotificationService service = null;
	
	//if instantiated via ServiceManager by reflection, the contructor with Properties argument is preferred over the 0-arg constructor
	public NotificationServiceProvider(Properties config) {
		
	}

	@Override
	public boolean connect(ComponentContext context, String connectionString) {
		service = new BdeVwsNotificationService(connectionString); 
		return isConnected();
	}

	@Override
	public void disconnect() {
		service = null;
	}

	@Override
	public boolean isConnected() {
		return service != null;
	}

	@Override
	public NotificationService getService() {
		return service;
	}
	
	@Override
	public BDEVWSNotification notify(BDEVWSNotification notification) {
		return service.notify(notification);
	}


}
