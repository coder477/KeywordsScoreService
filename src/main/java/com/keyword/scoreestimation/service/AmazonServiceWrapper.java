package com.keyword.scoreestimation.service;

import com.keyword.scoreestimation.controller.KeywordSearchController;
import com.keyword.scoreestimation.exception.BadRequestException;
import com.keyword.scoreestimation.exception.ExternalApiException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class AmazonServiceWrapper {

	private static final Logger log = getLogger(AmazonServiceWrapper.class);

	@Autowired
	private RestTemplate restTemplate;

	private final String AUTO_COMPLETE_API = "https://completion.amazon.com/search/complete?method=completion&mkt=1&search-alias=aps&q=";

	public Pair<String, List<String>> getKeyWordDataAsPair(String index) {
		try {

			log.info("Fetching keywords for prefix " + index);

			
			JSONArray responseArray = new JSONArray(
					restTemplate.getForObject(AUTO_COMPLETE_API.concat(index), String.class)
			).getJSONArray(1);

			List<String> amazonSearchWords = IntStream.range(0, responseArray.length())
					.mapToObj(responseArray::getString).collect(Collectors.toList());
			return new ImmutablePair<>(index, amazonSearchWords);
		} catch (HttpStatusCodeException httpException) {
			if (httpException.getStatusCode().is4xxClientError()) {
				throw new BadRequestException(httpException.getStatusCode(), httpException.getResponseBodyAsString());
			} else {
                throw new ExternalApiException("Failed calling the Auto Complete api", httpException.getResponseBodyAsString());
			}
		}
	}

}
