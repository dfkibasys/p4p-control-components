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

import java.io.IOException;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.walkthrough.common.entity.Alert;

public class OutOfRangeDetector extends KeyedProcessFunction<String, SignalData, Incident> {

	private static final long serialVersionUID = 1L;

	private static final String TOPIC = "roll1-current-rollforce";
	private static final double UPPER_BOUNDS = 1700.00;
	private static final double LOWER_BOUNDS = 10.00;
	private static final long DURATION = 3 * 1000; // 1 sek

	private transient ValueState<Boolean> flagState;
	private transient ValueState<Long> timerState;
	private transient ValueState<Double> valueState;

	@Override
	public void open(Configuration parameters) {
		ValueStateDescriptor<Boolean> flagDescriptor = new ValueStateDescriptor<>("flag", Types.BOOLEAN);
		flagState = getRuntimeContext().getState(flagDescriptor);

		ValueStateDescriptor<Long> timerDescriptor = new ValueStateDescriptor<>("timer-state", Types.LONG);
		timerState = getRuntimeContext().getState(timerDescriptor);
		
		ValueStateDescriptor<Double> valueDescriptor = new ValueStateDescriptor<>("value-state", Types.DOUBLE);
		valueState = getRuntimeContext().getState(valueDescriptor);
	}

	@Override
	public void processElement(SignalData data, Context context, Collector<Incident> collector) throws Exception {
		//System.out.println("process Element " + transaction.getAmount());
		// Get the current state for the current key
		Boolean lastDataPointWasOutOfBounds = flagState.value();
		Double lastValueOutOfRange = valueState.value();

		if (LOWER_BOUNDS <= data.getValue() && data.getValue() <= UPPER_BOUNDS ) { // in range
			if (lastDataPointWasOutOfBounds != null) {
				System.out.println(data.getId() + ": IN RANGE AGAIN " + data.getValue() + " ("  + context.timestamp() + ")");
				cleanUp(context);
			} else {
				// do nothing, still in range
			}
		} else  { // out of range
			if (lastDataPointWasOutOfBounds != null) {
				// do nothing, still out of range
			} else  {
				// set the flag to true

				System.out.println(data.getId() + ": OUT OF RANGE " + data.getValue() + " ("  + context.timestamp() + ")");
				flagState.update(true);
				valueState.update(data.getValue());
				long timer = context.timerService().currentProcessingTime() + DURATION;
				context.timerService().registerProcessingTimeTimer(timer);

				timerState.update(timer);
			}	
		}
	}

	@Override
	public void onTimer(long timestamp, OnTimerContext ctx, Collector<Incident> out) {
		//send alert after DURATION and clear state

		System.out.println(ctx.getCurrentKey() + ": OUT OF RANGE -> FIRE ALERT" + " ("  + ctx.timestamp() + ")");
		
		double value = 0;
		try {
			value = valueState.value().doubleValue();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Incident incident = new Incident();		
		incident.setSignalId(ctx.getCurrentKey());
		incident.setTopic(TOPIC);
		incident.setTimestamp(timestamp - DURATION);
		incident.setMessage("value out of range");
		incident.setValue(value);
		
		out.collect(incident);
		
		//timerState.clear();
		//flagState.clear();
	}

	private void cleanUp(Context ctx) throws Exception {
		// delete timer
		Long timer = timerState.value();
		ctx.timerService().deleteProcessingTimeTimer(timer);

		// clean up all state
		timerState.clear();
		flagState.clear();
		valueState.clear();
	}
}