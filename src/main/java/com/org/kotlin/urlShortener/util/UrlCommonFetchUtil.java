package com.org.kotlin.urlShortener.util;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.org.kotlin.urlShortener.dao.UrlDaoImpl;
import com.org.kotlin.urlShortener.exception.InvalidParameterException;
import com.org.kotlin.urlShortener.model.LongUrl;
import com.org.kotlin.urlShortener.model.Url;

@Component
public class UrlCommonFetchUtil {

	@Autowired
	private UrlDaoImpl lUrlDaoImpl;
	public static String EXPIRE_DATE = "EXPIRE_DATE";
	public static String RANDOM_URL = "RANDOM_URL";
	public static String localAddress = "http://localhost:8080/api/url-shortener/";
	public static Cache<String, HashMap<String, Object>> urlCache = null;
	public static Cache<String, String> shortUrlCache = null;
	int cacheServiceMaxEntries = 20000;
	int cacheExpireTimePeriod = 24;

	@PostConstruct
	public void fetchAllUrlFromDb() {
		List<Url> lUrlList = lUrlDaoImpl.fetchAllUrl();
		for (Url url : lUrlList) {
			HashMap<String, Object> lMapUrl = new HashMap<>();
			lMapUrl.put(EXPIRE_DATE, url.getExpireDateTime());
			lMapUrl.put(RANDOM_URL, url.getShortUrl());
			urlCache = CacheBuilder.newBuilder().maximumSize(cacheServiceMaxEntries)
					.expireAfterAccess(cacheExpireTimePeriod, TimeUnit.HOURS).build();
			urlCache.put(url.getLongUrl(), lMapUrl);
			shortUrlCache = CacheBuilder.newBuilder().maximumSize(cacheServiceMaxEntries)
					.expireAfterAccess(cacheExpireTimePeriod, TimeUnit.HOURS).build();
			shortUrlCache.put(url.getShortUrl(), url.getLongUrl());
		}
	}

	public boolean validateUrl(String url) {
		try {
			new URL(url).toURI();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void validateLongUrl(LongUrl lLongUrl) {

		if (lLongUrl == null || !StringUtils.hasText(lLongUrl.getFull_url())) {
			throw new InvalidParameterException("Long Url is empty");
		}
		if (!validateUrl(lLongUrl.getFull_url())) {
			throw new InvalidParameterException("Invalid Url : " + lLongUrl.getFull_url());
		}
	}

	public String randomString() {
		String randomStr = "";
		String possibleChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		for (int i = 0; i < 5; i++)
			randomStr += possibleChars.charAt((int) Math.floor(Math.random() * possibleChars.length()));
		return randomStr;
	}

	public boolean validateExpireDate(LocalDateTime lExpiredate) {
		if (LocalDateTime.now().isBefore(lExpiredate)) {
			return true;
		}
		return false;
	}

}
