/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dfki.cos.basys.p4p.platform.bdewvs.flink;

import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.walkthrough.common.sink.AlertSink;
import org.apache.flink.walkthrough.common.entity.Alert;
import org.apache.flink.walkthrough.common.entity.Transaction;
import org.apache.flink.walkthrough.common.source.TransactionSource;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

/**
 * Skeleton code for the datastream walkthrough
 */
public class OutOfRangeDetectionJob {
	public static void main(String[] args) throws Exception {
		
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		Properties consumerConfig = getDefaultConsumerConfig();		
		consumerConfig.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "OutOfRangeDetectionJob");

		Properties producerConfig = getDefaultProducerConfig();
		
	    String inputTopic = "roll1-current-rollforce"; // basysbdevws_avg_1 // 1 = ms , 10 = 1 sek
	    String outputTopic = "incidents";
		
		FlinkKafkaConsumer<SignalData> signalSource = new FlinkKafkaConsumer<>(inputTopic, new SignalDataDeserialisationSchema(), consumerConfig);
		signalSource.setStartFromLatest();
		
		FlinkKafkaProducer<Incident> incidentSink = new FlinkKafkaProducer<Incident>(outputTopic, new IncidentSerialisationSchema(outputTopic), producerConfig, FlinkKafkaProducer.Semantic.AT_LEAST_ONCE);
				
		DataStream<SignalData> signalData = env
			.addSource(signalSource)
			.name("signal-data");

		DataStream<Incident> alerts = signalData.keyBy(new KeySelector<SignalData,String>() {
		
			private static final long serialVersionUID = 1L;

			@Override
			public String getKey(SignalData value) throws Exception {
				return value.getId();
			}}).process(new OutOfRangeDetector())
			.name("fraud-detector");

		alerts
			.addSink(incidentSink)
			.name("reported-incidents");

		env.execute("OutOfRange Detection");
	}
	
    public static Properties getDefaultConsumerConfig(){
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092" );
        //props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        //props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "default_"+ Math.random()*10);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        //props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }
    
    public static Properties getDefaultProducerConfig(){
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        //props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaProducer_"+ Math.random()*100);
        return props;
    }
}
