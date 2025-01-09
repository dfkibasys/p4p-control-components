package de.dfki.cos.basys.p4p.controlcomponent.smartwatch.v2.service;

import java.util.List;

public interface SmartwatchService {

	TaskState getTaskState();
	void requestTaskExecution(TaskRequest request);
	void displayInfoMessage(String message);


	void reset();
}