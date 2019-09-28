package com.keyword.scoreestimation.service;

import java.util.List;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyword.scoreestimation.exception.BadRequestException;
import com.keyword.scoreestimation.exception.ExternalApiException;

@Component
public class AmazonServiceWrapper {
	
	@Autowired
	private RestTemplate restTemplate;

//	@Autowired
//	private ObjectMapper objectMapper;

	private final String AUTO_COMPLETE_API = "https://completion.amazon.com/search/complete?method=completion&mkt=1&search-alias=aps&q=";
	
	/**
     * Getting autocomplete data from the source API
     */
	
	public JSONArray getKeyWordData(String keyword) {

		HttpHeaders requestHeaders = new HttpHeaders();
		HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(AUTO_COMPLETE_API)
				.queryParam("method", "completion")
				.queryParam("mkt", "1")
				.queryParam("search-alias", "aps")
				.queryParam("q", keyword);
		try {
			//https://completion.amazon.com/search/complete?method=completion&mkt=1&search-alias=aps&q=iphone

		    String result = restTemplate.getForObject(AUTO_COMPLETE_API.concat(keyword), String.class);

			return new JSONArray(result).getJSONArray(1);
		} catch (HttpStatusCodeException httpException) {
			if (httpException.getStatusCode().is4xxClientError()) {
				throw new BadRequestException(httpException.getStatusCode(), httpException.getResponseBodyAsString());
			} else {
                throw new ExternalApiException("Failed calling the Auto Complete api", httpException.getResponseBodyAsString());
			}
		}
	}

}
