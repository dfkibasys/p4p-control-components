package de.dfki.cos.basys.p4p.platform.bdevws.submodels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProviderHelper;

import com.google.common.collect.Lists;

import de.dfki.cos.basys.aas.component.AasComponentContext;
import de.dfki.cos.basys.aas.component.SubmodelComponent;
import de.dfki.cos.basys.aas.component.impl.BaseSubmodelComponent;
import de.dfki.cos.basys.common.component.ComponentException;
import de.dfki.cos.basys.common.component.impl.BaseComponent;
import de.dfki.cos.basys.controlcomponent.util.Strings;
import de.dfki.cos.basys.platform.runtime.component.BasysComponent;

public class CurrentSignalDataSubmodel extends BaseSubmodelComponent {


	public final String signalSubmodelSemanticId = "urn:basys:org.eclipse.basyx:submodels:SignalSubmodel:1.0.0";
	
	public CurrentSignalDataSubmodel(Properties config) {
		super(config);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void doActivate() throws ComponentException {		
		super.doActivate();
		extendSubmodel(sm);
	}

	@Override
	protected void doDeactivate() throws ComponentException {
		
	}
	
	private void extendSubmodel(Submodel submodel) {
		
		SubmodelElementCollection signals = new SubmodelElementCollection("Signals");
		
		ConnectedAssetAdministrationShellManager aasManager = new ConnectedAssetAdministrationShellManager(((AasComponentContext) context).getAasRegistry());
		Identifier signalSubmodelId = new Identifier(IdentifierType.IRI,config.getProperty("signal.submodel.id", ""));
		ISubmodel signalSubmodel = aasManager.retrieveSubmodel(getAasId(), signalSubmodelId);
		

		LOGGER.debug(signalSubmodel.getIdShort());
		
		ISubmodelElementCollection _brokers = (ISubmodelElementCollection) signalSubmodel.getSubmodelElement("MessageBroker").getLocalCopy();
		ISubmodelElementCollection _signals = (ISubmodelElementCollection) signalSubmodel.getSubmodelElement("Signals").getLocalCopy();

		for (ISubmodelElement iterable_element : _signals.getSubmodelElements().values()) {
			ISubmodelElementCollection _signal = (ISubmodelElementCollection) iterable_element;
			
			ISubmodelElementCollection _dataProvisionings = (SubmodelElementCollection)_signal.getSubmodelElement("DataProvisionings");
			if (_dataProvisionings.getSubmodelElements().size() > 0) {
				Property signal = new Property(_signal.getIdShort());
				signal.setValueType(ValueType.String);
				signal.setValue("{}");
				//signal.setValueId(ref);

				//signal.setDataSpecificationReferences(Collections.singletonList(new Reference(keys)));
				
				signals.addSubmodelElement(signal);
			}
		}
		
		

		submodel.addSubmodelElement(signals);
		
	}

	
	
	
	
	private static Property createProperty(String name, Supplier<Object> getter, ValueType type) {
		return createProperty(name, getter, null, type);
	}
	
	private static Property createProperty(String name, Supplier<Object> getter, Consumer<Object> setter, ValueType type) {
		Property property = new Property(name, type);
		// For lambda properties, the type has to be explicitly specified as it can not be retrieved from the given
		// supplier automatically
        property.set(VABLambdaProviderHelper.createSimple(getter, setter), type);
		return property;
	}
	
	private static Property createProperty(String name, Object value, ValueType type) {
		Property property = new Property(name, type);
        property.set(value, type);
		return property;
	}
	
	
//	private void iterateOverRegistry() {
//		List<AASDescriptor> aasDescriptors = context.getAasRegistry().lookupAll();
//		for (AASDescriptor aasDescriptor : aasDescriptors) {
//			for (SubmodelDescriptor smDescriptor : aasDescriptor.getSubmodelDescriptors()) {
//				if (smDescriptor.getSemanticId() != null) {
//					if (smDescriptor.getSemanticId().getKeys().size() == 1) {
//						IKey key = smDescriptor.getSemanticId().getKeys().get(0);
//						if (key.getValue().equals(semanticId)) {
//							//TODO: Do something
//						}
//					}
//				}
//			}
//		}		
//	}
	
}
