package com.unity3d.project.exception;


public class ProjectException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public ProjectException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

	public ProjectException() {
		super();
	}
}