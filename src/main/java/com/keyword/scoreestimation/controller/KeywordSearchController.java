package com.keyword.scoreestimation.controller;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.keyword.scoreestimation.exception.BadRequestException;
import com.keyword.scoreestimation.response.ErrorResponse;
import com.keyword.scoreestimation.service.KeywordSearchService;



@RestController
@RequestMapping("/")
public class KeywordSearchController {

	@Autowired
	private KeywordSearchService kwsService;



	private static final Logger log = getLogger(KeywordSearchController.class);

	

	@RequestMapping(method = RequestMethod.GET, value = "/estimate")
	public ResponseEntity getMothlyExchangeRateHistory(@RequestParam String keyword) {
		return ResponseEntity.ok(kwsService.estimateScore(keyword));
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

	@ExceptionHandler(NumberFormatException.class)
	public ResponseEntity handleNumberFormatException(NumberFormatException exception) {
		log.error("Exception while processing request", exception);
		return ResponseEntity.badRequest()
				.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Please verify the given Inputs.", exception.getMessage()));
	}

	@ExceptionHandler(IOException.class)
	public ResponseEntity handleIOException(IOException exception) {
		log.error("Exception while processing request", exception);
		return ResponseEntity.badRequest()
				.body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Please verify the given Input request.", exception.getMessage()));
	}

}
