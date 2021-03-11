package de.dfki.cos.basys.p4p.platform.bdevws.notificationservice.impl;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.eyeled.basyx.bdevws.notifications.entities.BDEVWSNotification;
import de.eyeled.basyx.bdevws.notifications.entities.enums.BDEVWSNotificationCriticality;
import de.eyeled.basyx.bdevws.notifications.entities.enums.BDEVWSNotificationRole;
import de.eyeled.basyx.bdevws.notifications.entities.enums.BDEVWSNotificationSkill;
import de.eyeled.basyx.bdevws.notifications.entities.enums.BDEVWSNotificationStatus;
import de.eyeled.basyx.bdevws.notifications.service.BdeVwsNotificationService;
import de.eyeled.basyx.bdevws.notifications.service.interfaces.NotificationService;

public class NotificationTest {

	private NotificationService service = null;
	private String connectionString = "http://192.168.178.31:8080/eye.betriebsdatenserver/Notifications";		
	
	@Before
	public void setUp() throws Exception {
		service = new BdeVwsNotificationService(connectionString); 
	}

	@After
	public void tearDown() throws Exception {
		service = null;
	}

	@Test
	public void testSendAlert() {
		Instant i = Instant.now();
		List<BDEVWSNotificationSkill> skills =  List.of(BDEVWSNotificationSkill.UNDEFINED);
		List<BDEVWSNotificationRole> roles =  List.of(BDEVWSNotificationRole.ALL);
		
		BDEVWSNotification notification = new BDEVWSNotification("testSendAlert", "signalId", 42, i.toEpochMilli(), BDEVWSNotificationCriticality.ALERT, BDEVWSNotificationStatus.OPEN, null, null, null, 0, roles, skills);
		
		BDEVWSNotification result = service.notify(notification);
	}

	@Test
	public void testSendWarning() {
		Instant i = Instant.now();
		List<BDEVWSNotificationSkill> skills =  List.of(BDEVWSNotificationSkill.UNDEFINED);
		List<BDEVWSNotificationRole> roles =  List.of(BDEVWSNotificationRole.ALL);
		
		BDEVWSNotification notification = new BDEVWSNotification("testSendWarning", "signalId", 42, i.toEpochMilli(), BDEVWSNotificationCriticality.WARNING, BDEVWSNotificationStatus.OPEN, null, null, null, 0, roles, skills);

		BDEVWSNotification result = service.notify(notification);
	}

	
	
}
