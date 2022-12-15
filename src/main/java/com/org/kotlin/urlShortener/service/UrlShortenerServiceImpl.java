package com.org.kotlin.urlShortener.service;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.org.kotlin.urlShortener.dao.UrlDaoImpl;
import com.org.kotlin.urlShortener.exception.InvalidParameterException;
import com.org.kotlin.urlShortener.exception.ObjectAlreadyPresentException;
import com.org.kotlin.urlShortener.model.LongUrl;
import com.org.kotlin.urlShortener.model.Url;
import com.org.kotlin.urlShortener.util.UrlCommonFetchUtil;

@Component
public class UrlShortenerServiceImpl implements UrlShortenerService {
   
	@Autowired
	private UrlDaoImpl lUrlDaoImpl; 
	
	@Autowired
	private UrlCommonFetchUtil lUrlCommonFetchUtil;
	
	@Override
	public String createShortUrl(LongUrl lLongUrl) {		
		lUrlCommonFetchUtil.validateLongUrl(lLongUrl);
	    HashMap<String, HashMap<String, Object>> lUrlMap = lUrlCommonFetchUtil.lUrlMap;
	    HashMap<String, Object> lMapUrl = lUrlMap.get(lLongUrl.getFull_url());
		if(lMapUrl != null && !lMapUrl.isEmpty())
		{
		  LocalDateTime expireDate = (LocalDateTime) lMapUrl.get(UrlCommonFetchUtil.EXPIRE_DATE);
		  if(LocalDateTime.now().isAfter(expireDate))
		  {
			  return saveUrlInDb(lLongUrl.getFull_url(),lUrlMap);
		  }
		  HashMap<String, Object> lShortUrlMap= lUrlMap.get(lLongUrl.getFull_url());
		  throw new ObjectAlreadyPresentException("Short Url is already present : "+UrlCommonFetchUtil.localAddress+lShortUrlMap.get(UrlCommonFetchUtil.RANDOM_URL));
		}
		return saveUrlInDb(lLongUrl.getFull_url(),lUrlMap);
	}

	private String saveUrlInDb(String full_url,HashMap<String, HashMap<String, Object>> lMapUrl) {
		  Url lUrl=new Url();
		  lUrl.setLongUrl(full_url);
		  lUrl.setCreationDateTime(LocalDateTime.now());
		  lUrl.setExpireDateTime(LocalDateTime.now().plusMonths(6));
		  lUrl.setShortUrl(lUrlCommonFetchUtil.randomString());
		   if (lUrlDaoImpl.saveUrl(lUrl, 0)) {
			    HashMap<String,Object> lMap=new HashMap<>();
			    lMap.put(UrlCommonFetchUtil.EXPIRE_DATE, lUrl.getExpireDateTime());
			    lMap.put(UrlCommonFetchUtil.RANDOM_URL, lUrl.getShortUrl());
			    lMapUrl.put(full_url, lMap);
				return lUrl.getShortUrl();
			} else {
				throw new InvalidParameterException("Invalid Data ,try after sometime");
			}
	}

	@Override
	public String fetchShortUrl(String lLongUrl) {
		Url lUrl = lUrlDaoImpl.fetchUrlDetails("longUrl", lLongUrl);
		if(lUrl != null)
		{
			if(LocalDateTime.now().isAfter(lUrl.getExpireDateTime()))
			{
				throw new InvalidParameterException("Short Url is expired,please generate new short Url");
			}
			return UrlCommonFetchUtil.localAddress+""+lUrl.getShortUrl();
		}
		return null;
	}

	@Override
	public String fetchLongUrl(String lShortUrl) {
		Url lUrl=lUrlDaoImpl.fetchUrlDetails("shortUrl", lShortUrl);
		if(lUrl != null)
		{
			if(LocalDateTime.now().isAfter(lUrl.getExpireDateTime()))
			{
				throw new InvalidParameterException("Short Url is expired,please generate new short Url");
			}
			return lUrl.getLongUrl();
		}
		return null;
	}

	
}
