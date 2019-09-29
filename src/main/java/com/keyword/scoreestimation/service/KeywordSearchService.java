package com.keyword.scoreestimation.service;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.keyword.scoreestimation.response.KeyWordScoreResponse;

@Component
public class KeywordSearchService {

	private static final Logger log = getLogger(KeywordSearchService.class);

	@Autowired
	private AmazonServiceWrapper amazonServiceWrapper;

	private ExecutorService service = Executors.newCachedThreadPool();

	private static final Integer TIME_OUT_LIMIT = 10;
	
	private static final Integer MAX_SCORE = 100;

	public KeyWordScoreResponse estimateScore(String keyword) throws InterruptedException {
		return new KeyWordScoreResponse(keyword, getScoreForKeyWord(keyword));
	}

	private Double getScoreForKeyWord(String keyword) throws InterruptedException {
		List<String> prefixes = getPrefixesArray(keyword);

		List<Callable<Pair<String, List<String>>>> callables = new ArrayList<>();
		for (String prefix : prefixes) {
			callables.add(() -> amazonServiceWrapper.getKeyWordDataAsPair(prefix));
		}

		List<Future<Pair<String, List<String>>>> futures = service.invokeAll(callables, TIME_OUT_LIMIT,
				TimeUnit.SECONDS);

		double numerator = IntStream.range(0, prefixes.size()).mapToObj(i -> {
			try {
				return futures.get(i).get();
			} catch (Exception e) {
				log.error("Failed to query amazon for prefix " + prefixes.get(i));
				return null;
			}
		}).filter(Objects::nonNull)
				.flatMapToDouble(x -> x.getRight().stream().mapToDouble(y -> getPrefixSum(y, x.getKey(), keyword)))
				.sum();
		double denominator = 5.0D * ((keyword.length() * keyword.length()) + keyword.length());
		return numerator * MAX_SCORE / denominator;
	}

	private Double getPrefixSum(String awsWord, String prefix, String keyword) {
		double prefixWeight = (keyword.length() - prefix.length() + 1);
		if (awsWord.startsWith(keyword)) {
			return 1.0 * prefixWeight;
		} else {
			return getPartialScore(awsWord, prefix, keyword) * prefixWeight;
		}
	}

	private Double getPartialScore(String awsWord, String prefix, String keyword) {
		String[] awsArray = awsWord.split(" ");
		String[] keyWordArray = keyword.split(" ");
		int minLen = Math.min(awsArray.length, keyWordArray.length);
		int matchCount = 0;
		for (int i = 0; i < minLen; i++) {
			if (awsArray[i].equalsIgnoreCase(keyWordArray[i])) {
				matchCount++;
			}
		}
		return 1.0 * matchCount / awsArray.length;
	}

	private List<String> getPrefixesArray(String str) {

		return IntStream.range(0, str.length()).mapToObj(i -> str.substring(0, i + 1)).collect(Collectors.toList());

	}

}
