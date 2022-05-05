package de.dfki.cos.basys.p4p.controlcomponent.virtual.service;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaProducerFactory {


    public static <K, V> KafkaProducer<K, V> createAvroProducer(Properties serviceConfig){
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serviceConfig.getProperty("serviceConnectionString"));
        props.put(ProducerConfig.CLIENT_ID_CONFIG, serviceConfig.getProperty("id") + "_pose_producer");
        //props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, serviceConfig.getProperty("schemaRegistryUrl"));
        var producer = new KafkaProducer<K, V>(props);

        return producer;
    }

    public static KafkaProducer<String, String> createStringProducer(Properties serviceConfig){
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serviceConfig.getProperty("serviceConnectionString"));
        props.put(ProducerConfig.CLIENT_ID_CONFIG, serviceConfig.getProperty("id") + "_string_producer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        var producer = new KafkaProducer<String, String>(props);

        return producer;
    }
}
