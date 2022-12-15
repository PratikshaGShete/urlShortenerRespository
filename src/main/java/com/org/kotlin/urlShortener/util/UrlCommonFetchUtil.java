package com.org.kotlin.urlShortener.util;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.org.kotlin.urlShortener.dao.UrlDaoImpl;
import com.org.kotlin.urlShortener.exception.InvalidParameterException;
import com.org.kotlin.urlShortener.model.LongUrl;
import com.org.kotlin.urlShortener.model.Url;
import java.time.LocalDateTime;

@Component
public class UrlCommonFetchUtil {
	
	@Autowired
	private UrlDaoImpl lUrlDaoImpl;
	
	public HashMap<String,HashMap<String,Object>> lUrlMap=new HashMap<>();
	
	public static String EXPIRE_DATE = "EXPIRE_DATE";
	public static String RANDOM_URL = "RANDOM_URL";
	public static String localAddress="http://localhost:8080/api/url-shortener/";
	
	@PostConstruct
	public void fetchAllUrlFromDb()
	{
		List<Url> lUrlList = lUrlDaoImpl.fetchAllUrl();
		for(Url url:lUrlList)
		{
			HashMap<String,Object> lMapUrl=new HashMap<>();
			lMapUrl.put(EXPIRE_DATE, url.getExpireDateTime());
			lMapUrl.put(RANDOM_URL, url.getShortUrl());
			lUrlMap.put(url.getLongUrl(),lMapUrl);
		} 
	}
	
	public boolean validateUrl(String url)
	{
		try {
            new URL(url).toURI();
            return true;
        }
        catch (Exception e) {
            return false;
        }
	}

	public void validateLongUrl(LongUrl lLongUrl) {
		
		if(lLongUrl==null || !StringUtils.hasText(lLongUrl.getFull_url()))
		{
			throw new InvalidParameterException("Long Url is empty");
		}
		if(!validateUrl(lLongUrl.getFull_url()))
		{
			throw new InvalidParameterException("Invalid Url : "+lLongUrl.getFull_url());
		}		
	}

	public String randomString() {
		String randomStr = "";
		String possibleChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		for (int i = 0; i < 5; i++)
			randomStr += possibleChars.charAt((int) Math.floor(Math.random() * possibleChars.length()));
		return randomStr;
	}
	
	public boolean validateExpireDate(LocalDateTime lExpiredate)
	{		
		if (LocalDateTime.now().isBefore(lExpiredate)) {
			return true;
		}
		return false;
	}
	

}
