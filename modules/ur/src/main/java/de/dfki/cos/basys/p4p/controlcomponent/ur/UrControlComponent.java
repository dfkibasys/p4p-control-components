package de.dfki.cos.basys.p4p.controlcomponent.ur;

import java.util.Properties;

import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.ur.opmodes.MoveToSymbolicPositionOperationMode;
import de.dfki.cos.basys.p4p.controlcomponent.ur.service.URService;

public class UrControlComponent extends BaseControlComponent<URService> {

	public UrControlComponent(Properties config) {
		super(config);
	}
	
	@Override
	protected void registerOperationModes() {		
		registerOperationMode(new MoveToSymbolicPositionOperationMode(this));
	}
	
}
