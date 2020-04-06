package com.zulkarnaen.bot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "date", "deaths", "confirmed", "active", "recovered" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoronaBotTimeline {

	@JsonProperty("date")
	private String date;

	@JsonProperty("deaths")
	private int deaths;

	@JsonProperty("confirmed")
	private int confirmed;

	@JsonProperty("active")
	private int active;

	@JsonProperty("recovered")
	private int recovered;

	@JsonProperty("date")
	public String getDate() {
		return date;
	}

	@JsonProperty("date")
	public void setDate(String date) {
		this.date = date;
	}

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

	@JsonProperty("active")
	public int getActive() {
		return active;
	}

	@JsonProperty("active")
	public void setActive(int active) {
		this.active = active;
	}

	@JsonProperty("recovered")
	public int getRecovered() {
		return recovered;
	}

	@JsonProperty("recovered")
	public void setRecovered(int recovered) {
		this.recovered = recovered;
	}

}
