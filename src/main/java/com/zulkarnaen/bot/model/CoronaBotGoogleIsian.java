package com.zulkarnaen.bot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "source", "author", "title", "url", "urlToImage", "publishedAt", "content" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoronaBotGoogleIsian {

	@JsonProperty("source")
	private CoronaBotGoogleSource source;

	@JsonProperty("author")
	private String author;

	@JsonProperty("title")
	private String title;

	@JsonProperty("url")
	private String url;

	@JsonProperty("urlToImage")
	private String urlToImage;

	@JsonProperty("publishedAt")
	private String publishedAt;

	@JsonProperty("content")
	private String content;

	@JsonProperty("source")
	public CoronaBotGoogleSource getSource() {
		return source;
	}

	@JsonProperty("source")
	public void setSource(CoronaBotGoogleSource source) {
		this.source = source;
	}

	@JsonProperty("author")
	public String getAuthor() {
		return author;
	}

	@JsonProperty("author")
	public void setAuthor(String author) {
		this.author = author;
	}

	@JsonProperty("title")
	public String getTitle() {
		return title;
	}

	@JsonProperty("title")
	public void setTitle(String title) {
		this.title = title;
	}

	@JsonProperty("url")
	public String getUrl() {
		return url;
	}

	@JsonProperty("url")
	public void setUrl(String url) {
		this.url = url;
	}

	@JsonProperty("urlToImage")
	public String getUrlToImage() {
		return urlToImage;
	}

	@JsonProperty("urlToImage")
	public void setUrlToImage(String urlToImage) {
		this.urlToImage = urlToImage;
	}

	@JsonProperty("publishedAt")
	public String getPublishedAt() {
		return publishedAt;
	}

	@JsonProperty("publishedAt")
	public void setPublishedAt(String publishedAt) {
		this.publishedAt = publishedAt;
	}

	@JsonProperty("content")
	public String getContent() {
		return content;
	}

	@JsonProperty("content")
	public void setContent(String content) {
		this.content = content;
	}

}
