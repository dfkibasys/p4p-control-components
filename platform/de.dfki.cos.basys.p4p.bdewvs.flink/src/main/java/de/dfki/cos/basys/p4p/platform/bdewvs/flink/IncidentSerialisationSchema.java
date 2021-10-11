package de.dfki.cos.basys.p4p.platform.bdewvs.flink;

import javax.annotation.Nullable;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

public class IncidentSerialisationSchema implements KafkaSerializationSchema<Incident>  {

	private static final long serialVersionUID = 1L;
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private String topic;
	
	public IncidentSerialisationSchema(String topic) {
		this.topic = topic;
	}
	
	@Override
	public ProducerRecord<byte[], byte[]> serialize(Incident incident, @Nullable final Long timestamp) {
		try {
			//if topic is null, default topic will be used
			return new ProducerRecord<>(topic, objectMapper.writeValueAsBytes(incident));
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Could not serialize record: " + incident, e);
		}
	}

}
