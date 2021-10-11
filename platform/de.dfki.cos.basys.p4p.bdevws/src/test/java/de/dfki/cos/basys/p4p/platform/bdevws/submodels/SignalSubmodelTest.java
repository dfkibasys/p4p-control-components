package de.dfki.cos.basys.p4p.platform.bdevws.submodels;

import static org.junit.Assert.*;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SignalSubmodelTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {

		String registryUrl = "http://localhost:4999/api/v1/registry";
		String aasId = "urn:com.iba-ag:shells:1.0.0:ibadaq#walzgerüst";
		String submodelId = "urn:com.iba-ag:submodels:Signals:1.0.0:ibadaq#walzgerüst";
		
		IAASRegistry aasRegistry = new AASRegistryProxy(registryUrl);
		ConnectedAssetAdministrationShellManager aasManager = new ConnectedAssetAdministrationShellManager(aasRegistry);
		
		ISubmodel signalSubmodel = aasManager.retrieveSubmodel(new Identifier(IdentifierType.IRI, aasId), new Identifier(IdentifierType.IRI, submodelId));
		
		assertEquals("Signals", signalSubmodel.getIdShort());

		ISubmodelElementCollection _signals = (ISubmodelElementCollection) signalSubmodel.getSubmodelElement("Signals").getLocalCopy();

		for (ISubmodelElement iterable_element : _signals.getSubmodelElements().values()) {
			ISubmodelElementCollection _signal = (ISubmodelElementCollection) iterable_element;
			
			ISubmodelElementCollection _dataProvisionings = (SubmodelElementCollection)_signal.getSubmodelElement("DataProvisionings");
			if (_dataProvisionings.getSubmodelElements().size() > 0) {
				Property signal = new Property(_signal.getIdShort(), "");
				signal.setValueType(ValueType.String);
				signal.setValue("{}");
				//signal.setValueId(ref);

				//signal.setDataSpecificationReferences(Collections.singletonList(new Reference(keys)));

				System.out.println(_signal.getIdShort());

				System.out.println(signal.getIdShort());
			}
		}
		
	}

}
