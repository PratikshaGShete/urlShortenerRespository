package com.org.kotlin.urlShortener.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="url",uniqueConstraints = @UniqueConstraint(columnNames = {"long_url", "short_url"}))
public class Url {

	@Id
	@Column(name="long_url",length = 1000)
	private String longUrl;
	
	@Column(name="short_url")
	private String shortUrl;
	
	private LocalDateTime creationDateTime;
	
	private LocalDateTime expireDateTime;
	
	public String getLongUrl() {
		return longUrl;
	}

	public void setLongUrl(String longUrl) {
		this.longUrl = longUrl;
	}

	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	public LocalDateTime getCreationDateTime() {
		return creationDateTime;
	}

	public void setCreationDateTime(LocalDateTime creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	public LocalDateTime getExpireDateTime() {
		return expireDateTime;
	}

	public void setExpireDateTime(LocalDateTime expireDateTime) {
		this.expireDateTime = expireDateTime;
	}
		
}
