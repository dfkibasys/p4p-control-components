package de.dfki.cos.basys.p4p.controlcomponent.binpicking.service;

import java.util.List;

public interface BinPickingService {
	void provideParts(String partType, int number, String targetLocation);
	void providePartsOrder(List<ProductPartRequest> order);
	MissionState getMissionState();
	WorkState getWorkState();
	void reset();
}