package com.zulkarnaen.bot.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "name", "code", "population", "updated_at", "today", "latest_data" })
public class CoronaBotDatum {

	@JsonProperty("name")
	private String name;

	@JsonProperty("code")
	private String code;

	@JsonProperty("population")
	private int population;

	@JsonProperty("updated_at")
	private int updated_at;

	@JsonProperty("today")
	private CoronaBotToday today;

	@JsonProperty("latest_data")
	private CoronaBotLatest latest_data;

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("code")
	public String getCode() {
		return code;
	}

	@JsonProperty("code")
	public void setCode(String code) {
		this.code = code;
	}

	@JsonProperty("population")
	public int getPopulation() {
		return population;
	}

	@JsonProperty("population")
	public void setPopulation(int population) {
		this.population = population;
	}

	@JsonProperty("updated_at")
	public int getUpdated_at() {
		return updated_at;
	}

	@JsonProperty("updated_at")
	public void setUpdated_at(int updated_at) {
		this.updated_at = updated_at;
	}

	@JsonProperty("today")
	public CoronaBotToday getToday() {
		return today;
	}

	@JsonProperty("today")
	public void setToday(CoronaBotToday today) {
		this.today = today;
	}

	@JsonProperty("latest_data")
	public CoronaBotLatest getLatest_data() {
		return latest_data;
	}

	@JsonProperty("latest_data")
	public void setLatest_data(CoronaBotLatest latest_data) {
		this.latest_data = latest_data;
	}

}