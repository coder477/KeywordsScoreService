package com.keyword.scoreestimation.response;

public class ErrorResponse {

	public ErrorResponse(int status, String title, String description) {
		this.status = status;
		this.description = description;
		this.title = title;
	}

	private int status;
	private String title;
	private String description;

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStatus() {
		return status;
	}
}
