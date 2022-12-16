package com.org.kotlin.urlShortener.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.org.kotlin.urlShortener.exception.InvalidParameterException;
import com.org.kotlin.urlShortener.model.LongUrl;
import com.org.kotlin.urlShortener.service.UrlShortenerServiceImpl;
import com.org.kotlin.urlShortener.util.UrlCommonFetchUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;


@RestController
public class UrlApiController  {
	
	private static final Logger logger = LoggerFactory.getLogger(UrlApiController.class);

    @Autowired
    private UrlShortenerServiceImpl lUrlShortenerServiceImpl;
    
    @ApiOperation(value = "API to create short Url")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Ok"),
	@ApiResponse(code = 201, message = "Object created"), @ApiResponse(code = 400, message = "Request invalid"),
	@ApiResponse(code = 409, message = "Object already exists"),
	@ApiResponse(code = 500, message = "Internal server error") })
    @RequestMapping(value = { "/api/url-shortener/url={url}" }, method = RequestMethod.POST)
	public ResponseEntity<String> createUrlShortForLongUrl(@Parameter(description = "long Url to generate the short Url") @Valid @RequestBody LongUrl longUrl)
	{
    	logger.info("Method : POST /api/url-shortener/url="+longUrl.getFull_url() +"/");
    	String shortUrl = lUrlShortenerServiceImpl.createShortUrl(longUrl);
		if (shortUrl != null) {
			return new ResponseEntity<String>(UrlCommonFetchUtil.localAddress+""+shortUrl, HttpStatus.CREATED);
		}
		return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ApiOperation(value = "", hidden = true)
	@RequestMapping(value="/api/url-shortener/{shortUrl}", method=RequestMethod.GET)
	public void redirectShortUrl(HttpServletResponse response, @PathVariable("shortUrl") String shortUrl) throws IOException {
		String longUrl = lUrlShortenerServiceImpl.fetchLongUrl(shortUrl);
		if (longUrl != null) {
			response.sendRedirect(longUrl);
		} else {
			throw new InvalidParameterException("Invalid Url");
		}
	}
	
	@ApiOperation(value = "API to get a Full URL.")
	@RequestMapping(value="/api/longurl/{shortUrl}", method=RequestMethod.GET)
	public ResponseEntity<String> fetchLongUrl(@PathVariable("shortUrl") String shortUrl) throws IOException {
		logger.info("Method : GET /api/longurl/"+shortUrl+"/");
		String longUrl = lUrlShortenerServiceImpl.fetchLongUrl(shortUrl);
		if (longUrl != null) {
			return new ResponseEntity<String>(longUrl, HttpStatus.OK);
		}
		return new ResponseEntity<String>("Short Url is not present",HttpStatus.NOT_FOUND);
	}
	
	

}
