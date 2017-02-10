package com.wj.bean;

import java.util.ArrayList;

public class HomePageResult {
	
	private String code;
	private ArrayList<String> homepage;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public ArrayList<String> getHomepage() {
		return homepage;
	}
	public void setHomepage(ArrayList<String> homepage) {
		this.homepage = homepage;
	}
	public HomePageResult(String code, ArrayList<String> homepage) {
		this.code = code;
		this.homepage = homepage;
	}
	public HomePageResult() {
	}
}
