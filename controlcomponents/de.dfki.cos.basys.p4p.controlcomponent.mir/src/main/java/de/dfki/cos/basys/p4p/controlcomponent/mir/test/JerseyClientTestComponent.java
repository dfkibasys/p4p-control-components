package de.dfki.cos.basys.p4p.controlcomponent.mir.test;

import java.util.Properties;
import java.util.function.Supplier;

import de.dfki.cos.basys.common.component.ComponentException;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.common.component.impl.ServiceComponent;

public class JerseyClientTestComponent extends ServiceComponent<JerseyClientTestService> {
	
	public JerseyClientTestComponent(Properties config) {
		super(config);

	}

	@Override
	protected void doActivate() throws ComponentException {	
		super.doActivate();
		
		getService().test();
		
	}

}
