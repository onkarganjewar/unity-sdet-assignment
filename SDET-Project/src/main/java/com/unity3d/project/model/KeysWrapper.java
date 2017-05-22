package com.unity3d.project.model;

public class KeysWrapper {

	private double number;
	private String keyword;

	public KeysWrapper() {
		// TODO Auto-generated constructor stub
	}
	
	
	public KeysWrapper(double number, String keyword) {
		super();
		this.number = number;
		this.keyword = keyword;
	}


	public double getNumber() {
		return number;
	}
	public void setNumber(double number) {
		this.number = number;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	@Override
	public String toString() {
		return "KeysWrapper [number=" + number + ", keyword=" + keyword + "]";
	}
	
}
