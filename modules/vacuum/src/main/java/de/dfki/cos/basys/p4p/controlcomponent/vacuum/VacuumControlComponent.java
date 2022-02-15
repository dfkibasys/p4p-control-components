package de.dfki.cos.basys.p4p.controlcomponent.vacuum;

import java.util.Properties;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.vacuum.opmodes.MoveToPointOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.vacuum.service.VacuumService;

public class VacuumControlComponent extends BaseControlComponent<VacuumService> {

	public VacuumControlComponent(Properties config) {
		super(config);
	}
	
	@Override
	protected void registerOperationModes() {		
		registerOperationMode(new MoveToPointOperationMode(this));
	}
	
}
