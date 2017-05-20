package com.unity3d.project.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Onkar Ganjewar
 */
public class Project {
	private int id;
	private String projectName;
	@JsonProperty
	private boolean enabled;
	private String creationDate;
	private String expiryDate;
	private List<String> targetCountries;
	private Double projectCost;
	private String projectUrl;
	private List<KeysWrapper> targetKeys;

	public Project() {

	}

	public Project(int id, String projectName, boolean enabled, String creationDate, String expiryDate,
			List<String> targetCountries, Double projectCost, String projectUrl, List<KeysWrapper> targetKeys) {
		super();
		this.id = id;
		this.projectName = projectName;
		this.enabled = enabled;
		this.creationDate = creationDate;
		this.expiryDate = expiryDate;
		this.targetCountries = targetCountries;
		this.projectCost = projectCost;
		this.projectUrl = projectUrl;
		this.targetKeys = targetKeys;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName
	 *            the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 *            the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public List<String> getTargetCountries() {
		return targetCountries;
	}

	public void setTargetCountries(List<String> targetCountries) {
		this.targetCountries = targetCountries;
	}

	public Double getProjectCost() {
		return projectCost;
	}

	public void setProjectCost(Double projectCost) {
		this.projectCost = projectCost;
	}

	public String getProjectUrl() {
		return projectUrl;
	}

	public void setProjectUrl(String projectUrl) {
		this.projectUrl = projectUrl;
	}

	public List<KeysWrapper> getTargetKeys() {
		return targetKeys;
	}

	public void setTargetKeys(List<KeysWrapper> targetKeys) {
		this.targetKeys = targetKeys;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Project [id=" + id + ", projectName=" + projectName + ", projectCost=" + projectCost + ", projectUrl=" + projectUrl + "]";
	}

}
