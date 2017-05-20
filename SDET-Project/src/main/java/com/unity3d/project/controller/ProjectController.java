package com.unity3d.project.controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.json.Json;
import javax.json.JsonObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unity3d.project.model.Project;

import ch.qos.logback.classic.pattern.color.HighlightingCompositeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Onkar Ganjewar
 */
@RestController
public class ProjectController {

	/** Logger **/
	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

	/** Database input file **/
	private static final String dataFile = "Projects.txt";

	@RequestMapping("/createproject")
	public @ResponseBody String createProject(@RequestBody Project project) throws Exception {

		// Convert the POJO into a json string format
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(project);

		// Save the input project data in a text file
		try (FileWriter file = new FileWriter("Projects.txt", true)) {
			file.write(json);
			String newline = System.getProperty("line.separator");
			file.append(newline);
			logger.info("Wrote record with Id = " + project.getId() + " successfully...");
			// System.out.println("Successfully Copied JSON Object to File...");
		}
		return "campaign is successfully created";
	}

	/**
	 * Returns the matched record from the database
	 * 
	 * @param id
	 *            Project ID to be searched.
	 * @param country
	 *            Contry name
	 * @param keyword
	 *            Keywords present
	 * @param projectCost
	 *            Project cost (number)
	 * @return
	 * @throws org.json.simple.parser.ParseException
	 */
	@RequestMapping("/requestproject")
	public @ResponseBody String getProject(@RequestParam(value = "projectid", required = false) Long id,
			@RequestParam(value = "country", required = false) String country,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "number", required = false) Long projectCost)
			throws org.json.simple.parser.ParseException {

		System.out.println(id + country + keyword + projectCost);
		Project returnProject = new Project();
		JsonObject returnVal;
		
		if (id != null) {
			// If project Id is present then return project with that Id
			// irrespective of other parameters
			returnProject = getProjectById(id);
			if (returnProject == null) {
				throw new NoSuchElementException("Record with Id = " + id + " does not exist!!!");
			} else {
				returnVal = Json.createObjectBuilder().add("projectName", returnProject.getProjectName())
						.add("projectCost", returnProject.getProjectCost()).add("projectUrl", returnProject.getProjectUrl())
						.build();
				return returnVal.toString();
			}
		} else if (country != null) {
			// Return project with highest cost out of selected ones. 
			// If any of the url param is not matched then should return no project found message
			if (searchProjectByCountry(country, projectCost, keyword)!= null) {
				returnProject = searchProjectByCountry(country, projectCost, keyword);
				returnVal = Json.createObjectBuilder().add("projectName", returnProject.getProjectName())
						.add("projectCost", returnProject.getProjectCost()).add("projectUrl", returnProject.getProjectUrl())
						.build();
				return returnVal.toString();
			}
		}
		
		returnVal = Json.createObjectBuilder().add("message", "no project found").build();
		return returnVal.toString();
	}

	/**
	 * Searches the database for the record with matched country/cost/keyword
	 * 
	 * @param country
	 *            Country name
	 * @param projectCost
	 *            Project cost (number)
	 * @param keyword
	 *            Keywords
	 * @return
	 */
	private Project searchProjectByCountry(String country, Long projectCost, String keyword) {

		/** Stores a list of project records **/
		List<Project> pList = new ArrayList<Project>();
		Project highCostProject = new Project();
		JsonObject returnVal;
		if (projectCost == null && keyword == null) {
			// Get projects by only country name
			highCostProject = getProjectsByTargetCountry(country);
			if (highCostProject != null) {
				// return project with highest cost in selected list of projects. 
				return highCostProject;
			}
		} else if (projectCost != null) {
			// Return project with highest cost out of selected ones
			getAllProjectsByCost(country, projectCost, keyword);
		} else if (keyword != null) {
			// Return project with highest cost out of selected ones. 
			// If any of the url param is not matched then should return no project found message
		}

		return null;
	}

	/**
	 * Search for the project with highest cost from the list of matching
	 * projects with the target country
	 * 
	 * @param country
	 *            Country name to be searched in the target countries list
	 * @return Project with highest cost
	 */
	private Project getProjectsByTargetCountry(String country) {

		/** Stores a list of project records **/
		List<Project> projectsList = new ArrayList<Project>();
		JSONObject obj;

		String line = null;
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(dataFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				Project project = new Gson().fromJson(line, Project.class);
				// ● Service should never return a project if projectUrl is null
				// ● Service should always return projects which are enabled,
				if (project.isEnabled() == false || project.getProjectUrl() == null || project.getExpiryDate() == null)
					continue;

				// ● Service should never return a project which is expired
				if (!checkExpiryDate(project.getExpiryDate()))
					continue;
				logger.info(project.toString());
				List<String> countries = project.getTargetCountries();
				for (String s : countries) {
					if (s.equalsIgnoreCase(country)) {
						logger.info("FOUND COUNTRY!!!!!!!!");
						projectsList.add(project);
						break;
					}
				}
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			logger.error(ex.getMessage());
			logger.debug("Unable to open file '" + dataFile + "'");
		} catch (IOException ex) {
			logger.error("Error reading file '" + dataFile + "'");
			// ex.printStackTrace();
		}

		if (projectsList.isEmpty())
			return null;
		Double pcost = 0.00;
		Project highCost = new Project();
		for (Project p : projectsList) {
			Long id;
			Double pc = p.getProjectCost();
			if (pc > pcost) {
				pcost = pc;
				id = (long) p.getId();
				highCost = p;
			}
		}
		return highCost;
	}

	/**
	 * Check whether today’s date is above expiry date.
	 * 
	 * @param expiryDate
	 * @return False, if today's date is above expiry date
	 */
	private boolean checkExpiryDate(String expiryDate) {

		String date = expiryDate;
		// Expiry date example --- "expiryDate ": "05202017 00:00:00"
		String[] splitDate = date.split(" ");
		String dateStr = splitDate[0];
		logger.debug(dateStr);
		
		// Get the hour/min/sec time from 00:00:00 format
		String[] splitTime = splitDate[1].split(":");
		StringBuffer hourStr = new StringBuffer();
		hourStr.append(splitTime[0]);
		int hourOfDay = Integer.parseInt(hourStr.toString());


		StringBuffer minStr = new StringBuffer();
		minStr.append(splitTime[1]);
		int minute = Integer.parseInt(minStr.toString());

		StringBuffer secStr = new StringBuffer();
		secStr.append(splitTime[2]);
		int second = Integer.parseInt(secStr.toString());

		
		// Fetch the mm/dd/yy from mmddyy input format
		char[] dateArr = dateStr.toCharArray();

		StringBuffer dayStr = new StringBuffer();
		dayStr.append(dateArr[2]);
		dayStr.append(dateArr[3]);
		int day = Integer.parseInt(dayStr.toString());

		StringBuffer monthStr = new StringBuffer();
		monthStr.append(dateArr[0]);
		monthStr.append(dateArr[1]);
		int month = Integer.parseInt(monthStr.toString());

		StringBuffer yearStr = new StringBuffer();
		yearStr.append(dateArr[4]);
		yearStr.append(dateArr[5]);
		yearStr.append(dateArr[6]);
		yearStr.append(dateArr[7]);
		int year = Integer.parseInt(yearStr.toString());
		// ● Service should never return a project which is expired
		String expDate = "Expiry Date = " + day + "/" + month + "/" + year;

		Calendar myCal = Calendar.getInstance();
		Date currDate = myCal.getTime();
		logger.debug(myCal.getTime().toString());
		logger.debug(currDate.toString());
		myCal.set(Calendar.YEAR, year);
		myCal.set(Calendar.MONTH, month-1);
		myCal.set(Calendar.DAY_OF_MONTH, day);
		myCal.set(year, month-1, day, hourOfDay, minute, second);
		Date expiry = myCal.getTime();
		logger.debug(expDate);
		logger.debug(currDate.toString());

		// If today’s date is above expiry date then project should not be
		// selected.
		if (currDate.after(expiry)) {
			logger.error("Project expired !!!!!");
			return false;
		}
		return true;
	}

	/**
	 * Returns the list of projects with same country name and cost/keyword
	 * 
	 * @param country
	 * @param projectCost
	 * @param keyword
	 */
	private List<JSONObject> getAllProjectsByCost(String country, Long projectCost, String keyword) {

		/** Stores a list of project records **/
		List<JSONObject> json = new ArrayList<JSONObject>();
		JSONObject obj = null;
		
		String line = null;
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(dataFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
				Project project = new Gson().fromJson(line, Project.class);
				// ● Service should never return a project if projectUrl is null
				// ● Service should always return projects which are enabled,
				if (project.isEnabled() == false || project.getProjectUrl() == null || project.getExpiryDate() == null)
					continue;

				// ● Service should never return a project which is expired
				if (!checkExpiryDate(project.getExpiryDate()))
					continue;
				logger.info(project.toString());
				JsonObject result = Json.createObjectBuilder().add("projectName", project.getProjectName()).add("projectCost", projectCost)
						.add("projectUrl", project.getProjectUrl()).build();
				json.add(obj);
				return json;
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			logger.error(ex.getMessage());
			logger.debug("Unable to open file '" + dataFile + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + dataFile + "'");
			// ex.printStackTrace();
		}

		return null;

	}

	/**
	 * Returns the matched project by Id
	 * 
	 * @param pId
	 *            Project Id to be searched
	 * @return Project record in json string
	 */
	private Project getProjectById(Long pId) {
		JSONObject obj;

		// This will reference one line at a time
		String line = null;

		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(dataFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				Project project = new Gson().fromJson(line, Project.class);
				// ● Service should never return a project if projectUrl is null
				// ● Service should always return projects which are enabled,
				if (project.isEnabled() == false || project.getProjectUrl() == null || project.getExpiryDate() == null)
					continue;

				// ● Service should never return a project which is expired
				if (!checkExpiryDate(project.getExpiryDate()))
					continue;
				logger.info(project.toString());

				if (project.getId() == pId) {
					return project;
				}
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			logger.error(ex.getMessage());
			logger.debug("Unable to open file '" + dataFile + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + dataFile + "'");
			// ex.printStackTrace();
		}
		return null;
	}

}
