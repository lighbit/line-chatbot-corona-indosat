package com.zulkarnaen.bot.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "articles" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoronaBotGoogleArticles {

	@JsonProperty("articles")
	private List<CoronaBotGoogleIsian> articles;

	@JsonProperty("articles")
	public List<CoronaBotGoogleIsian> getArticles() {
		return articles;
	}

	@JsonProperty("articles")
	public void setArticles(List<CoronaBotGoogleIsian> articles) {
		this.articles = articles;
	}

}
