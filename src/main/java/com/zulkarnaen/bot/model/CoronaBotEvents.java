package com.zulkarnaen.bot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "data" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoronaBotEvents {

	@JsonProperty("data")
	private CoronaBotDatum data = null;

	@JsonProperty("data")
	public CoronaBotDatum getData() {
		return data;
	}

	@JsonProperty("data")
	public void setData(CoronaBotDatum data) {
		this.data = data;
	}
}