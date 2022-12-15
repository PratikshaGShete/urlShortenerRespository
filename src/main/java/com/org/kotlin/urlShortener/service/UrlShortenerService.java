package com.org.kotlin.urlShortener.service;

import com.org.kotlin.urlShortener.model.LongUrl;

public interface UrlShortenerService {
	
	public String createShortUrl(LongUrl lLongUrl);
	
	public String fetchShortUrl(String lLongUrl);
	
	public String fetchLongUrl(String lShortUrl);
	
}
