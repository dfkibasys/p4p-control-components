package de.dfki.cos.basys.p4p.controlcomponent.worker;

import org.apache.thrift.TException;

import de.dfki.iui.hrc.hybritcommand.CommandState;
import de.dfki.iui.hrc.hybritcommand.HumanTaskDTO;

public interface NotificationService {
	CommandState requestTaskExecution(HumanTaskDTO task) throws TException;
	CommandState displayInfoMessage(String message) throws TException;
	CommandState getCommandState(String taskId) throws TException;
	void reconnect();
	void reset();
	
	//abort/cancelRequest()
}
