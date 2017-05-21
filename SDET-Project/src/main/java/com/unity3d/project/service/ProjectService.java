package com.unity3d.project.service;

import java.util.List;


import com.unity3d.project.model.Project;


public interface ProjectService {
	
	/**
	 * Return the project with matching ID regardless of any other parameters
	 * @param id ID of the project to be searched
	 * @return Project POJO if the object is found, else returns null
	 */
	public Project getProjectById(Long id);
	
	/**
	 * Return the list of Projects belonging to the same target country
	 * @param country Target country name
	 * @return Project List if there are any projects present, else returns null
	 */
	public List<Project> getProjectListByCountry(String country);
	
	
	/**
	 * Fetch all the projects stored in the database.
	 * @return Project List
	 * @throws IllegalArgumentException If the data input file is empty
	 */
	public List<Project> getAllProjects()throws IllegalArgumentException;
	
	
//	/**
//	 * Checks projects based on enability/expiration/projectUrl and returns the project w/ highest cost
//	 * @param projects Projects to filter
//	 * @return Project w/ highest value after meeting all the criteria
//	 * @throws NoSuchElementException If no project found fulfilling all the criteria
//	 */
//	public Project filterProjects (List<Project> projects) throws NoSuchElementException;


	/**
	 * Return the list of Projects having the given keyword
	 * @param keyword 
	 * @return Project List if there are any such projects present, else returns null
	 */
	public List<Project> getProjectByKeyword(String keyword);



	/**
	 * Returns the filtered list of Projects having number greater than the given number
	 * @param number Number to compare against
	 * @param projects List of projects to search for
	 * @return Filtered project list
	 */
	public List<Project> filterProjectListByNumberMin(List<Project> projects, double number);

}
