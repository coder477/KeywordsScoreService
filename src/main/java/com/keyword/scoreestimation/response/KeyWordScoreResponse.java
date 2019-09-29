package com.keyword.scoreestimation.response;

public class KeyWordScoreResponse {
	private String keyWord;
	private Double score;

	public KeyWordScoreResponse(String keyWord, Double score) {
		this.keyWord = keyWord;
		this.score = score;
	}
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
}
