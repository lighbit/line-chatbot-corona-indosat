package com.zulkarnaen.bot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "latitude", "longitude" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoronaBotCoordinates {

	@JsonProperty("latitude")
	private int latitude;

	@JsonProperty("longitude")
	private int longitude;

	@JsonProperty("latitude")
	public int getLatitude() {
		return latitude;
	}

	@JsonProperty("latitude")
	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	@JsonProperty("longitude")
	public int getLongitude() {
		return longitude;
	}

	@JsonProperty("longitude")
	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

}
