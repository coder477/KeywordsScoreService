package com.keyword.scoreestimation.controller;

import com.keyword.scoreestimation.exception.BadRequestException;
import com.keyword.scoreestimation.response.ErrorResponse;
import com.keyword.scoreestimation.service.KeywordSearchService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/")
public class KeywordSearchController {

	@Autowired
	private KeywordSearchService kwsService;

	private static final Logger log = getLogger(KeywordSearchController.class);

	@RequestMapping(method = RequestMethod.GET, value = "/estimate")
	public ResponseEntity getMothlyExchangeRateHistory(@RequestParam String keyword) {
		if (StringUtils.isBlank(keyword)) {
			throw new BadRequestException(BAD_REQUEST, "Please input valid keyword");
		}
		try {
			return ResponseEntity.ok(kwsService.estimateScore(keyword));
		} catch (InterruptedException e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 *
	 * Exception handlers for this controller are added below.
	 */

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity handleBadRequestException(BadRequestException exception) {
		log.error("Exception while processing request", exception);
		return ResponseEntity.badRequest().body(new ErrorResponse(exception.getValue().value(),
				"Please verify the given Inputs. ", exception.getMessage()));
	}
}
