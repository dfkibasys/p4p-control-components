package de.dfki.cos.basys.p4p.platform.bdevws.incidentservice.impl;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.dfki.cos.basys.common.component.ComponentContext;
import de.dfki.cos.basys.common.component.ServiceProvider;
import de.dfki.cos.basys.common.rest.camunda.ApiException;
import de.dfki.cos.basys.common.rest.camunda.api.DeploymentApi;
import de.dfki.cos.basys.common.rest.camunda.api.MessageApi;
import de.dfki.cos.basys.common.rest.camunda.api.ProcessDefinitionApi;
import de.dfki.cos.basys.common.rest.camunda.dto.CorrelationMessageDto;
import de.dfki.cos.basys.common.rest.camunda.dto.ProcessInstanceWithVariablesDto;
import de.dfki.cos.basys.common.rest.camunda.dto.StartProcessInstanceDto;
import de.dfki.cos.basys.common.rest.camunda.dto.VariableValueDto;
import de.dfki.cos.basys.p4p.platform.bdevws.incidentservice.Incident;
import de.dfki.cos.basys.p4p.platform.bdevws.incidentservice.IncidentTreatmentService;

public class IncidentTreatmentServiceImpl implements ServiceProvider<IncidentTreatmentService>, IncidentTreatmentService {
	private ProcessDefinitionApi api = null;
	private MessageApi messageApi = null;
	private String camundaEndpoint;	
	private	Consumer<String, String> consumer = null;
	private boolean isConnected = false;
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	public IncidentTreatmentServiceImpl(Properties config) {
		camundaEndpoint = config.getProperty("camundaEndpoint");
		if (camundaEndpoint.endsWith("/"))
			camundaEndpoint = camundaEndpoint.substring(0, camundaEndpoint.length()-1);	
	}
	
	@Override
	public boolean connect(ComponentContext context, String connectionString) {
			
		api = new ProcessDefinitionApi();
		api.getApiClient().setBasePath(camundaEndpoint);
		messageApi = new MessageApi();
		messageApi.getApiClient().setBasePath(camundaEndpoint);
		
		Properties config = getDefaultConsumerConfig();
		config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, connectionString);
		
	    consumer = new KafkaConsumer<>(config);
	    consumer.subscribe(Collections.singletonList("incidents")); 
		isConnected = true;
		
		context.getScheduledExecutorService().schedule(new Runnable() {
			
			@Override
			public void run() {
				while (isConnected()) {
					ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

					records.forEach(record -> {						
						System.out.printf("Consumer Record:(%d, %s, %d, %d)\n", record.key(), record.value(),record.partition(), record.offset());
						try {
							Incident incident = objectMapper.readValue(record.value(), Incident.class);
							sendMessage("new-incident", incident);
						} catch (JsonMappingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JsonProcessingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					});
				    consumer.commitAsync();
				}				
			}
		}, 1000, TimeUnit.MILLISECONDS);
		
	    
	    return isConnected;
	}

	@Override
	public void disconnect() {
		consumer.close();
		isConnected = false;
	}

	@Override
	public boolean isConnected() {		
		return isConnected;
	}

	@Override
	public IncidentTreatmentService getService() {
		return this;
	}

	public void sendMessage(String message, Incident incident) {
		CorrelationMessageDto correlationMessageDto = new CorrelationMessageDto();
		correlationMessageDto.setMessageName("new-incident");
		correlationMessageDto.putProcessVariablesItem("incidentMessage", new VariableValueDto().type("String").value(incident.getMessage()));
		correlationMessageDto.putProcessVariablesItem("incidentSignalId", new VariableValueDto().type("String").value(incident.getSignalId()));
		//correlationMessageDto.putProcessVariablesItem("criticality", new VariableValueDto().type("String").value("WARNING"));
		//correlationMessageDto.putProcessVariablesItem("status", new VariableValueDto().type("String").value("OPEN"));
		correlationMessageDto.putProcessVariablesItem("incidentTimestamp", new VariableValueDto().type("String").value(incident.getTimestamp()));
		correlationMessageDto.putProcessVariablesItem("incidentValue", new VariableValueDto().type("String").value(incident.getValue()));
//		System.out.println(correlationMessageDto);
		try {
			messageApi.deliverMessage(correlationMessageDto);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
	public void createProcessInstance(String processDefinitionId, Incident incident) {
		StartProcessInstanceDto startProcessInstanceDto = new StartProcessInstanceDto();
		startProcessInstanceDto.putVariablesItem("incidentMessage", new VariableValueDto().type("String").value(incident.getMessage()));
		startProcessInstanceDto.putVariablesItem("incidentSignalId", new VariableValueDto().type("String").value(incident.getSignalId()));
		//startProcessInstanceDto.putVariablesItem("criticality", new VariableValueDto().type("String").value("WARNING"));
		//startProcessInstanceDto.putVariablesItem("status", new VariableValueDto().type("String").value("OPEN"));
		startProcessInstanceDto.putVariablesItem("incidentTimestamp", new VariableValueDto().type("String").value(incident.getTimestamp()));
		startProcessInstanceDto.putVariablesItem("incidentValue", new VariableValueDto().type("String").value(incident.getValue()));
				
		try {
			ProcessInstanceWithVariablesDto result = api.startProcessInstance(processDefinitionId, startProcessInstanceDto);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public static Properties getDefaultConsumerConfig(){
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092" );
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "default_"+ Math.random()*10);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        //props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }
    
//    public static Properties getDefaultProducerConfig(){
//        Properties props = new Properties();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "lns-90165.sb.dfki.de:9092");
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        //props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaProducer_"+ Math.random()*100);
//        return props;
//    }
}
