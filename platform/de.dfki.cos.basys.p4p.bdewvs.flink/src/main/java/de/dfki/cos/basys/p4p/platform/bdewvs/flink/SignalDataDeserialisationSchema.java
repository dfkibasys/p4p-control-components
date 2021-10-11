package de.dfki.cos.basys.p4p.platform.bdewvs.flink;

import java.io.IOException;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

public class SignalDataDeserialisationSchema implements DeserializationSchema<SignalData> {
	
	private static final long serialVersionUID = 1L;

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public TypeInformation<SignalData> getProducedType() {
		return TypeInformation.of(SignalData.class);
	}

	@Override
	public SignalData deserialize(byte[] message) throws IOException {
		return objectMapper.readValue(message, SignalData.class);
	}

	@Override
	public boolean isEndOfStream(SignalData nextElement) {		
		return false;
	}
}
