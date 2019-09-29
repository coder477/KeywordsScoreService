package com.keyword.scoreestimation.service;

import com.keyword.scoreestimation.response.KeyWordScoreResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class KeywordSearchService {

	private static final Logger log = getLogger(KeywordSearchService.class);

	@Autowired
	private AmazonServiceWrapper amazonServiceWrapper;

	private ExecutorService service = Executors.newCachedThreadPool();

	public KeyWordScoreResponse estimateScore(String keyword) throws InterruptedException{
		return new KeyWordScoreResponse(keyword, getScoreForKeyWord(keyword));
	}

	private Double getScoreForKeyWord(String keyword) throws InterruptedException {
		List<String> prefixes = getPrefixesArray(keyword);

		List<Callable<Pair<String, List<String>>>> callables = prefixes
				.stream()
				.<Callable<Pair<String, List<String>>>>map(
						p -> () -> amazonServiceWrapper.getKeyWordDataAsPair(p)
				).collect(Collectors.toList());

		List<Future<Pair<String, List<String>>>> futures = service
				.invokeAll(callables, 10, TimeUnit.SECONDS);

		double numerator = futures.stream()
				.map(f -> {
					try {
						return f.get();
					} catch (Exception e) {
						log.error("Failed to query amazon for prefix");
						return null;
					}
				})
				.filter(Objects::nonNull)
				.flatMapToDouble(x -> x.getRight().stream().mapToDouble(y -> getPrefixSum(y, x.getKey(), keyword)))
				.sum();
		double denominator = 5.0D * ((keyword.length() * keyword.length()) + keyword.length());
		return numerator * 100 / denominator;
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
