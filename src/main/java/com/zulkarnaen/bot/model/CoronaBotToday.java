package com.zulkarnaen.bot.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "deaths", "confirmed" })
public class CoronaBotToday {

	@JsonProperty("deaths")
	private int deaths;

	@JsonProperty("confirmed")
	private int confirmed;

	@JsonProperty("deaths")
	public int getDeaths() {
		return deaths;
	}

	@JsonProperty("deaths")
	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	@JsonProperty("confirmed")
	public int getConfirmed() {
		return confirmed;
	}

	@JsonProperty("confirmed")
	public void setConfirmed(int confirmed) {
		this.confirmed = confirmed;
	}

}
