package com.keyword.scoreestimation.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keyword.scoreestimation.response.KeyWordScoreResponse;

@Component
public class KeywordSearchService {

	@Autowired
	private AmazonServiceWrapper amazonServiceWrapper;

	public KeyWordScoreResponse estimateScore(String keyword) {

		KeyWordScoreResponse response = new KeyWordScoreResponse();
		Double score = getScore(keyword);
		response.setKeyWord(keyword);
		response.setScore(score);

		return response;
	}

	private Double getScore(String keyword) {

		double finalScore = 0;

		List<String> prefixes = getPrefixesArray(keyword);
		// List<JSONArray> autocompletewords=new ArrayList<JSONArray>();
		Map<String, Double> prefixScores = new HashMap<String, Double>();
		for (String prefix : prefixes) {
			JSONArray autocompletewords = amazonServiceWrapper.getKeyWordData(prefix);
			Double prefixSum = 0.0;
			for (int i = 0; i < autocompletewords.length(); i++) {
				prefixSum = prefixSum + getPrefixSum(autocompletewords.getString(i), prefix, keyword);
				prefixScores.put(prefix, prefixSum);
			}
		}
		for (String prefix : prefixScores.keySet()) {
			finalScore = finalScore + prefixScores.get(prefix);

		}
		finalScore = 10*finalScore/prefixScores.size()  ;

		return (finalScore);
	}

	private Double getPrefixSum(String awsWord, String prefix, String keyword) {
		Double score = 0.0;
		if (awsWord.startsWith(keyword)) {
			score = 1.0 / (prefix.length());
		} else {
			score = 0.0;
		}
		return score;
	}

	private List<String> getPrefixesArray(String str) {
		List<String> prefixList = new ArrayList<String>();
		for (int i = 0; i < str.length(); i++) {
			prefixList.add(str.substring(0, i + 1));

		}
		return prefixList;
	}

}
