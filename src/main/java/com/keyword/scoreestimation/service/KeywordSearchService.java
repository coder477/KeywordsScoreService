package com.keyword.scoreestimation.service;

import com.keyword.scoreestimation.response.KeyWordScoreResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
public class KeywordSearchService {

	@Autowired
	private AmazonServiceWrapper amazonServiceWrapper;

	private ExecutorService service = Executors.newCachedThreadPool();

	public KeyWordScoreResponse estimateScore(String keyword) throws InterruptedException{
		return new KeyWordScoreResponse(keyword, getScore(keyword));
	}

	private Double getScore(String keyword) throws InterruptedException{
		double finalScore = 0;
		List<String> prefixes = getPrefixesArray(keyword);
		Map<String, Double> prefixScores = new HashMap<String, Double>();


		List<Callable<List<String>>> callables = new ArrayList<>();

		for (String prefix: prefixes) {
			callables.add(() -> amazonServiceWrapper.getKeyWordData(prefix));
		}
		List<Future<List<String>>> futures = service.invokeAll(callables, 10, TimeUnit.SECONDS);

		for (int i = 0; i < prefixes.size(); i++) {
			List<String> words = null;
			try {
				words = futures.get(i).get();
			} catch (Exception e) {
				// need good exception handling
				System.out.println(e.getMessage());
			}
			Double prefixSum = 0.0;
			for (int j = 0; j < words.size(); j++) {
				prefixSum = prefixSum + getPrefixSum(words.get(j), prefixes.get(i), keyword);
				prefixScores.put( prefixes.get(i), prefixSum);
			}
		}
		for (String prefix : prefixScores.keySet()) {
			finalScore = finalScore + prefixScores.get(prefix);

		}
		finalScore = finalScore/ (5*(keyword.length()*keyword.length()+keyword.length()));

		return (finalScore)*100;
	}

	private Double getPrefixSum(String awsWord, String prefix, String keyword) {
		Double score = 0.0;
		if (awsWord.startsWith(keyword)) {
			score = 1.0 * (keyword.length() - prefix.length()+1);
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
