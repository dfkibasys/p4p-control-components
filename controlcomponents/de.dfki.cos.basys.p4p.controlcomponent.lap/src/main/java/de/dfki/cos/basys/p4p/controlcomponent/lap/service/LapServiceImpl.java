package de.dfki.cos.basys.p4p.controlcomponent.lap.service;

import java.util.List;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.p4p.controlcomponent.lap.service.dto.*;

public class LapServiceImpl implements LapService, ServiceProvider<LapService> {

	protected final Logger LOGGER = LoggerFactory.getLogger(LapServiceImpl.class.getName());
	private boolean connected = false;
	private String missionState = "pending";
	WebTarget resource = null;
	
	public LapServiceImpl(Properties config) {

	}
	
	@Override
	public boolean connect(ComponentContext context, String connectionString) {
		
		Client client = ClientBuilder.newClient();
		resource = client.target(connectionString);

		StopProjectionRequest spr = new StopProjectionRequest();

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(spr));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			connected = true;
		}
		else {
			connected = false;
		}
		
		return connected;
	}

	@Override
	public void disconnect() {
		connected = false;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public LapService getService() {
		return this;
	}

	@Override
	public void projectRectangle(double x, double y, double z, int color, double width, double height) {
		
		missionState = "executing";
		
		RectangleProjectionRequest rpr = new RectangleProjectionRequest(x, y, z, color, width, height);
		
		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(rpr));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void projectString(double x, double y, double z, int color, String text, double height) {
		missionState = "executing";

		StringProjectionRequest spr = new StringProjectionRequest(x, y, z, color, text, height);

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(spr));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void projectChar(double x, double y, double z, int color, String chr, double height) {
		missionState = "executing";
		
		CharProjectionRequest cpr = new CharProjectionRequest(x, y, z, color, chr, height);

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(cpr));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void projectCircle(double x, double y, double z, int color, double radius, double angleStart,
			double angleLength) {
		missionState = "executing";

		CircleProjectionRequest cpr = new CircleProjectionRequest(x, y, z, color, radius, angleStart, angleLength);

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(cpr));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void projectEllipse(double x, double y, double z, int color, double majorRadius, double minorRadius,
			double angleStart, double angleLength) {
		missionState = "executing";

		EllipseProjectionRequest epr = new EllipseProjectionRequest(x, y, z, color, majorRadius, minorRadius, angleStart, angleLength);
				
		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(epr));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void projectLine(double x, double y, double z, int color, double x2, double y2, double z2) {
		missionState = "executing";
		
		LineProjectionRequest lpr = new LineProjectionRequest(x, y, z, color, x2, y2, z2);

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(lpr));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void projectMovingArrows(double x, double y, double z, int color, List<Point> points, int arrowCount,
			int delay) {
		missionState = "executing";
		
		MovingArrowsProjectionRequest mapr = new MovingArrowsProjectionRequest(x, y, z, color, points, arrowCount, delay);

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(mapr));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void projectMovingETA(double x, double y, double z, int color, double radius, double angle, double fullTime,
			double startTime) {
		missionState = "executing";

		MovingETAProjectionRequest mepr = new MovingETAProjectionRequest(x, y, z, color, radius, angle, fullTime, startTime);

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(mepr));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void projectPulsatingCircle(double x, double y, double z, int color, double innerCircleRadius,
			double middleCircleRadius, double outerCircleRadius, double angleStart, double angleLength, int delay) {
		missionState = "executing";

		PulsatingCirclesProjectionRequest pcpr = new PulsatingCirclesProjectionRequest(x, y, z, color, innerCircleRadius, middleCircleRadius, outerCircleRadius, angleStart, angleLength, delay);

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(pcpr));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void projectArrowsAndCircles(double x, double y, double z, int color, List<Point> points, int arrowCount,
			double innerCircleRadius, double middleCircleRadius, double outerCircleRadius, double angleStart,
			double angleLength, int delayArrows, int delayCircles) {
		missionState = "executing";
		
		ArrowsAndCirclesProjectionRequest acpr = new ArrowsAndCirclesProjectionRequest(x, y, z, color, points, arrowCount, innerCircleRadius, middleCircleRadius, outerCircleRadius, angleStart, angleLength, delayArrows, delayCircles);
		
		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(acpr));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}

	@Override
	public void stopProjection() {
		missionState = "executing";	

		StopProjectionRequest spr = new StopProjectionRequest();

		Response response = resource.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.json(spr));
		
		if (response.getStatus() == Status.OK.getStatusCode()) {
			missionState = "done";
		}
		else {
			missionState = "failed";
		}
		
	}
	
	@Override
	public void reset() {
		missionState = "pending";
		resource = null;
	}

	@Override
	public void abort() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getMissionState() {
		return missionState;
	}

	@Override
	public String getWorkState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
