package com.unity3d.project.exception;

import java.util.NoSuchElementException;

import javax.json.Json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	/** Logger **/
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	/**
	 * Handler for custom "project exception"
	 * @param ex Exception occured 
	 * @return A message "project not found" to user in json 
	 */
	@ExceptionHandler(ProjectException.class)
	public ResponseEntity<Object> exceptionProjectHandler(Exception ex) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String json = Json.createObjectBuilder()
	            .add("message", "no project found")
	            .build()
	            .toString();
		logger.error("ProjectException occurred with message = "+ex.getMessage());
		return new ResponseEntity<Object>(json, headers, HttpStatus.BAD_REQUEST);
	}
	
	
	/**
	 * Handler to handle exceptions of type NoSuchElementException 
	 * @param ex Exception occurred
	 * @return Error message in json received from the controller
	 */
	@ExceptionHandler({ NoSuchElementException.class })
	public ResponseEntity<Object> exceptionHandler(Exception ex) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String json = Json.createObjectBuilder()
	            .add("message", ex.getMessage())
	            .build()
	            .toString();
		logger.error("NoSuchElementException occurred with message = "+ex.getMessage());
		return new ResponseEntity<Object>(json, headers, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * Custom handler to handle IllegalArgumentException exceptions occurred due to user input errors
	 * @param e Exception occurred
	 * @return Error message in json received from the controller 
	 */
	@ExceptionHandler({ IllegalArgumentException.class })
	protected ResponseEntity<Object> handleInvalidRequest(RuntimeException e) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String json = Json.createObjectBuilder()
				.add("message", e.getMessage())
				.build()
				.toString();

		logger.error("IllegalArgumentException occurred "+e.getMessage());
		return new ResponseEntity<Object>(json, headers, HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
//	public ResponseEntity<Object> handleNoSuchElementException(Exception ex, WebRequest request) {
//		String jsonMessage = Json.createObjectBuilder().add("message", ex.getMessage()).build().toString();
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//      extends ResponseEntityExceptionHandler 
//		return handleExceptionInternal(ex, jsonMessage, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
//	}

}