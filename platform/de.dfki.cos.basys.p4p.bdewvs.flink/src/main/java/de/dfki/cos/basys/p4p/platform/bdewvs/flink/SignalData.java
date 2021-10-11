package de.dfki.cos.basys.p4p.platform.bdewvs.flink;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

public class SignalData {

	@JsonProperty("Signal") 
	private String id;

	@JsonProperty("Value") 
	private double value;

	@JsonProperty("Timestamp") //2021-04-27T12:55:05.2857022Z
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss.SSSSSSS'Z'")
	private Date timestamp;

	public SignalData() { }

	public SignalData(String id, Date timestamp, double amount) {
		this.id = id;
		this.timestamp = timestamp;
		this.value = amount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SignalData that = (SignalData) o;
		return id == that.id &&
			timestamp == that.timestamp &&
			Double.compare(that.value, value) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, timestamp, value);
	}

	@Override
	public String toString() {
		return "SignalData{" +
			"id=" + id +
			", timestamp=" + timestamp +
			", value=" + value +
			'}';
	}

}
