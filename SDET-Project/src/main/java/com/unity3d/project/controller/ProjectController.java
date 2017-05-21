package com.unity3d.project.controller;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.json.Json;
import javax.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unity3d.project.model.KeysWrapper;
import com.unity3d.project.model.Project;
import com.unity3d.project.service.ProjectService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Onkar Ganjewar
 */
@RestController
public class ProjectController {

	@Autowired
	private ProjectService ps;

	/** Logger **/
	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

	@RequestMapping("/hello")
	@ResponseBody public String gethelloWorld() {
		return "Hello World!";
	}

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
	@RequestMapping(value = "/requestproject", produces = "application/json")
	public @ResponseBody String getProject(@RequestParam(value = "projectid", required = false) Long id,
			@RequestParam(value = "country", required = false) String country,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "number", required = false) Long number)
			throws org.json.simple.parser.ParseException {

		System.out.println(id + country + keyword + number);
		Project returnProject = new Project();
		JsonObject returnVal;

		if (id == null && country == null && keyword == null && number == null) {
			List<Project> allProjects = ps.getAllProjects();
			if (!allProjects.isEmpty()) {
				returnProject = (filterProjectsByCost(allProjects));
				returnVal = Json.createObjectBuilder().add("projectName", returnProject.getProjectName())
						.add("projectCost", returnProject.getProjectCost())
						.add("projectUrl", returnProject.getProjectUrl()).build();

				return returnVal.toString();
			}
		} else if (id != null) {
			// If project Id is present then return project with that Id
			// irrespective of other parameters
			returnProject = ps.getProjectById(id);
			if (returnProject == null) {
				throw new NoSuchElementException("Record with Id = " + id + " does not exist!!!");
			} else {
				returnVal = Json.createObjectBuilder().add("projectName", returnProject.getProjectName())
						.add("projectCost", returnProject.getProjectCost())
						.add("projectUrl", returnProject.getProjectUrl()).build();

				return returnVal.toString();
			}
		} else if (country != null) {
			// Return project with highest cost out of selected ones.
			// If any of the url param is not matched then should return no
			// project found message
			if (searchProjectByCountry(country, number, keyword) != null) {
				returnProject = searchProjectByCountry(country, number, keyword);
				returnVal = Json.createObjectBuilder().add("projectName", returnProject.getProjectName())
						.add("projectCost", returnProject.getProjectCost())
						.add("projectUrl", returnProject.getProjectUrl()).build();
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
	 * @param number
	 *            Number (targetKeys)
	 * @param keyword
	 *            Keywords
	 * @return Project having highest cost after filtering
	 */
	private Project searchProjectByCountry(String country, Long number, String keyword) {

		/** Stores a list of project records **/
		List<Project> pList = new ArrayList<Project>();
		// Get projects by only country name
		pList = ps.getProjectListByCountry(country);

		// Retrieve all the results for the target country
		if (pList == null) {
			throw new NoSuchElementException("Records not found with given target country name");
		}

		// 1. Country Name Present
		// 2. Country Name && KeyWord present
		// 3. Country Name && Number present
		// 4. Country Name && KeyWord && Number present

		// 1. Country Name Present
		if (number == null && keyword == null) {
			// Filter the results based on highest cost
			return (filterProjectsByCost(pList));
		}

		// 2. Country Name && KeyWord present
		else if (number != null && keyword == null) {
			List<Project> projects = filterProjectListByNumber(pList, number);
			if (projects != null) {
				return (filterProjectsByCost(projects));
			} else
				throw new NoSuchElementException("Records not found with number greater than or equal to " + number
						+ " and target country name = " + country);
		}

		// 3. Country Name && Number present
		else if (keyword != null && number == null) {
			List<Project> projects = filterProjectListByKeyWord(pList, keyword);
			if (!projects.isEmpty()) {
				return (filterProjectsByCost(projects));
			} else
				throw new NoSuchElementException(
						"Records not found with keyword  = " + keyword + " and target country name = " + country);
		}

		// 4. Country Name && KeyWord && Number present
		else {
			// Match keyword and number w/ given projects (country name)
			List<Project> returnProjects = findProjectNumKey(pList, keyword, number);
			if (!returnProjects.isEmpty())
				return (filterProjectsByCost(returnProjects));
			return null;
		}
	}

	/**
	 * Returns the matched project(s) with exact keyword and number
	 * 
	 * @param pList
	 *            List to be traversed
	 * @param number
	 *            Number to be greather than
	 * @param keyword
	 *            Keyword to be matched
	 * @return Exact matched project(s)
	 */
	private List<Project> findProjectNumKey(List<Project> pList, String keyword, Long number) {
		// TODO Auto-generated method stub
		List<Project> projectsList = new ArrayList<Project>();

		for (Project p : pList) {
			List<KeysWrapper> keyset = p.getTargetKeys();
			for (KeysWrapper k : keyset) {
				if (k.getNumber() >= number && k.getKeyword().equalsIgnoreCase(keyword)) {
					logger.info("FOUND EXACT MATCH!!!!!!!!");
					projectsList.add(p);
				}
			}
		}
		return projectsList;
	}

	/**
	 * Returns the filtered list of Projects having number greater than the
	 * given number
	 * 
	 * @param projects
	 *            List of projects to search for
	 * @param number
	 *            Number to compare against
	 * @return Filtered project list
	 */
	private List<Project> filterProjectListByNumber(List<Project> projects, Long number) {
		/** Stores a list of project records **/
		List<Project> projectsList = new ArrayList<Project>();
		for (Project p : projects) {
			List<KeysWrapper> keyset = p.getTargetKeys();
			for (KeysWrapper k : keyset) {
				if (k.getNumber() >= number) {
					logger.info("FOUND Greater Number!!!!!!!!");
					projectsList.add(p);
					break;
				}
			}
		}

		if (projectsList.isEmpty())
			return null;
		return projectsList;

	}

	/**
	 * Filters the project based on the matching keywords from targetkeys
	 * 
	 * @param projects
	 *            Projects to traverse through
	 * @param keyword
	 *            Keyword to match
	 * @return Projects having same keyword
	 */
	private List<Project> filterProjectListByKeyWord(List<Project> projects, String keyword) {

		List<Project> returnList = new ArrayList<Project>();
		for (Project p : projects) {
			List<KeysWrapper> keyset = p.getTargetKeys();
			for (KeysWrapper k : keyset) {
				if (k.getKeyword().equalsIgnoreCase(keyword)) {
					logger.info("FOUND Keyword!!!!!!!!");
					returnList.add(p);
				}
			}
		}
		return returnList;
	}

	/**
	 * Filters the project list to give project w/ highest cost
	 * 
	 * @param pList
	 *            Project list to be searched for
	 * @return Project with max cost
	 */
	private Project filterProjectsByCost(List<Project> pList) {

		Project highCostProject = new Project();
		Double pcost = 0.00;
		for (Project p : pList) {
			Double pc = p.getProjectCost();
			// return project with highest cost in selected list of
			// projects.
			if (pc > pcost) {
				pcost = pc;
				highCostProject = p;
			}
		}
		return highCostProject;
	}

}
