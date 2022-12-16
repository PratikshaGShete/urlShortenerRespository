package com.org.kotlin.urlShortener.schedule;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;
import com.org.kotlin.urlShortener.dao.UrlDaoImpl;
import com.org.kotlin.urlShortener.model.Url;
import com.org.kotlin.urlShortener.util.UrlCommonFetchUtil;

@Component
public class ScheduleConfiguration {

	@Autowired
	private UrlDaoImpl lUrlDaoImpl;

	@Autowired
	private UrlCommonFetchUtil lUrlCommonFetchUtil;

	@Scheduled(cron = "0 30 4 15 * ?")
	public void doScheduledWork() {

		List<Url> lUrlList = lUrlDaoImpl.fetchAllUrl();
		Set<String> lUrlSet = new HashSet<>();
		for (Url url : lUrlList) {
			Cache<String, HashMap<String, Object>> lUrlMap = lUrlCommonFetchUtil.urlCache;
			lUrlMap.cleanUp();
			Cache<String, String> lShortUrlMap = lUrlCommonFetchUtil.shortUrlCache;
			lShortUrlMap.cleanUp();
			if (!lUrlCommonFetchUtil.validateUrl(url.getLongUrl())) {
				lUrlSet.add(url.getLongUrl());
			} else if (!lUrlCommonFetchUtil.validateExpireDate(url.getExpireDateTime())) {
				Url lUrl = new Url();
				lUrl.setLongUrl(url.getLongUrl());
				lUrl.setCreationDateTime(LocalDateTime.now());
				lUrl.setExpireDateTime(LocalDateTime.now().plusMonths(6));
				lUrl.setShortUrl(lUrlCommonFetchUtil.randomString());
				lUrlDaoImpl.saveUrl(lUrl, 0);
				lUrlMap.put(url.getLongUrl(), populateHashMapForUrl(url));
				lShortUrlMap.put(url.getShortUrl(), url.getLongUrl());

			} else {
				lUrlMap.put(url.getLongUrl(), populateHashMapForUrl(url));
				lShortUrlMap.put(url.getShortUrl(), url.getLongUrl());
			}
		}
		if (!lUrlSet.isEmpty()) {
			lUrlDaoImpl.deleteUrlFromDb(lUrlSet);
		}

	}

	private HashMap<String, Object> populateHashMapForUrl(Url url) {
		HashMap<String, Object> lMap = new HashMap<String, Object>();
		lMap.put(UrlCommonFetchUtil.EXPIRE_DATE, url.getExpireDateTime());
		lMap.put(UrlCommonFetchUtil.RANDOM_URL, url.getShortUrl());
		return lMap;
	}

}
