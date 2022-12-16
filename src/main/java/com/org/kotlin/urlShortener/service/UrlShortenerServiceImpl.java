package com.org.kotlin.urlShortener.service;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.org.kotlin.urlShortener.dao.UrlDaoImpl;
import com.org.kotlin.urlShortener.exception.InvalidParameterException;
import com.org.kotlin.urlShortener.exception.ObjectAlreadyPresentException;
import com.org.kotlin.urlShortener.model.LongUrl;
import com.org.kotlin.urlShortener.model.Url;
import com.org.kotlin.urlShortener.util.UrlCommonFetchUtil;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

	@Autowired
	private UrlDaoImpl lUrlDaoImpl;
	
	@Autowired
	private UrlCommonFetchUtil lUrlCommonFetchUtil;

	@Override
	public String createShortUrl(LongUrl lLongUrl) {
		lUrlCommonFetchUtil.validateLongUrl(lLongUrl);
		HashMap<String, Object> lMapUrl = lUrlCommonFetchUtil.urlCache.getIfPresent(lLongUrl.getFull_url());
		if (lMapUrl != null && !lMapUrl.isEmpty()) {
			LocalDateTime expireDate = (LocalDateTime) lMapUrl.get(UrlCommonFetchUtil.EXPIRE_DATE);
			if (LocalDateTime.now().isAfter(expireDate)) {
				return saveUrlInDb(lLongUrl.getFull_url());
			}
			throw new ObjectAlreadyPresentException("Short Url is already present : " + UrlCommonFetchUtil.localAddress
					+ lMapUrl.get(UrlCommonFetchUtil.RANDOM_URL));
		} else {
			Url lUrl = lUrlDaoImpl.fetchUrlDetails("longUrl", lLongUrl.getFull_url());
			if (lUrl != null) {
				throw new ObjectAlreadyPresentException(
						"Short Url is already present : " + UrlCommonFetchUtil.localAddress + lUrl.getShortUrl());
			}
		}
		return saveUrlInDb(lLongUrl.getFull_url());
	}

	private String saveUrlInDb(String full_url) {
		Url lUrl = new Url();
		lUrl.setLongUrl(full_url);
		lUrl.setCreationDateTime(LocalDateTime.now());
		lUrl.setExpireDateTime(LocalDateTime.now().plusMonths(6));
		lUrl.setShortUrl(lUrlCommonFetchUtil.randomString());
		if (lUrlDaoImpl.saveUrl(lUrl, 0)) {
			HashMap<String, Object> lMap = new HashMap<>();
			lMap.put(UrlCommonFetchUtil.EXPIRE_DATE, lUrl.getExpireDateTime());
			lMap.put(UrlCommonFetchUtil.RANDOM_URL, lUrl.getShortUrl());
			lUrlCommonFetchUtil.urlCache.put(full_url, lMap);
			lUrlCommonFetchUtil.shortUrlCache.put(lUrl.getShortUrl(), full_url);
			return lUrl.getShortUrl();
		} else {
			throw new InvalidParameterException("Invalid Data ,try after sometime");
		}
	}

	@Override
	public String fetchShortUrl(String lLongUrl) {
		Url lUrl = lUrlDaoImpl.fetchUrlDetails("longUrl", lLongUrl);
		if (lUrl != null) {
			if (LocalDateTime.now().isAfter(lUrl.getExpireDateTime())) {
				throw new InvalidParameterException("Short Url is expired,please generate new short Url");
			}
			return UrlCommonFetchUtil.localAddress + "" + lUrl.getShortUrl();
		}
		return null;
	}

	@Override
	public String fetchLongUrl(String lShortUrl) {
		String lLongUrl = lUrlCommonFetchUtil.shortUrlCache.getIfPresent(lShortUrl);
		if (StringUtils.hasText(lLongUrl)) {
			return lLongUrl;
		} else {
			Url lUrl = lUrlDaoImpl.fetchUrlDetails("shortUrl", lShortUrl);
			if (lUrl != null) {
				if (LocalDateTime.now().isAfter(lUrl.getExpireDateTime())) {
					throw new InvalidParameterException("Short Url is expired,please generate new short Url");
				}
				return lUrl.getLongUrl();
			}
			return null;
		}
	}

}
