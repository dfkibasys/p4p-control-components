package de.dfki.cos.basys.p4p.controlcomponent.mir;

import java.util.Properties;

import de.dfki.cos.basys.common.rest.mir.MirService;
import de.dfki.cos.basys.controlcomponent.OperationMode;
import de.dfki.cos.basys.controlcomponent.impl.BaseControlComponent;
import de.dfki.cos.basys.p4p.controlcomponent.mir.opmodes.MoveToSymbolicPositionOperationMode;

public class MirControlComponent extends BaseControlComponent<MirService> {

	public MirControlComponent(Properties config) {
		super(config);		
	}
	
	@Override
	protected void registerOperationModes() {		
		OperationMode opMode = new MoveToSymbolicPositionOperationMode(this);
		registerOperationMode(opMode);		
	}
	
}
