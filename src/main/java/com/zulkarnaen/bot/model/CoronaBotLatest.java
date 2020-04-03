package com.zulkarnaen.bot.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "deaths", "confirmed", "critical", "critical" })
public class CoronaBotLatest {

	@JsonProperty("deaths")
	private int deaths;

	@JsonProperty("confirmed")
	private int confirmed;

	@JsonProperty("recovered")
	private int recovered;

	@JsonProperty("critical")
	private int critical;

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

	@JsonProperty("recovered")
	public int getRecovered() {
		return recovered;
	}

	@JsonProperty("recovered")
	public void setRecovered(int recovered) {
		this.recovered = recovered;
	}

	@JsonProperty("critical")
	public int getCritical() {
		return critical;
	}

	@JsonProperty("critical")
	public void setCritical(int critical) {
		this.critical = critical;
	}

}
