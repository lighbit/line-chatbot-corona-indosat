package com.zulkarnaen.bot.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "success", "data" })
public class CoronaBotEvents {

	@JsonProperty("success")
	private boolean success;

	@JsonProperty("data")
	private List<CoronaBotDatum> data = null;

	@JsonProperty("success")
	public boolean isSuccess() {
		return success;
	}

	@JsonProperty("success")
	public void setSuccess(boolean success) {
		this.success = success;
	}

	@JsonProperty("data")
	public List<CoronaBotDatum> getData() {
		return data;
	}

	@JsonProperty("data")
	public void setData(List<CoronaBotDatum> data) {
		this.data = data;
	}
}