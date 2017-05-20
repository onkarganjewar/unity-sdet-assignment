package com.unity3d.project.controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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

/**
 * @author Onkar Ganjewar
 */
@RestController
public class ProjectController {

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
			System.out.println("Successfully Copied JSON Object to File...");
		}
		return "campaign is successfully created";
	}

	@RequestMapping("/requestproject")
	public @ResponseBody String getProject(@RequestParam(value = "projectid", required = false) Long id,
			@RequestParam(value = "country", required = false) String country,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "number", required = false) Long projectCost) throws org.json.simple.parser.ParseException {

		System.out.println(id + country + keyword + projectCost);
		ArrayList<JSONObject> json = new ArrayList<JSONObject>();
		JSONObject obj;
		
		Long pId = id;
		// This will reference one line at a time
		String line = null;
		String fileName = "Projects.txt";

		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(fileName);

			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
				obj = (JSONObject) new JSONParser().parse(line);
				json.add(obj);
				Long projectId = (Long) obj.get("id");
				if (projectId == pId) {
					String pName = (String) obj.get("projectName");
					System.out.println(projectId + ":" + pName );
					String projectUrl = (String) obj.get("projectUrl"); 
					Double cost = (Double) obj.get("projectCost");
					System.out.println(cost + projectUrl);	
					JsonObject result = Json.createObjectBuilder()
			                .add("projectName", pName)
			                .add("projectCost", cost)
			                .add("projectUrl", projectUrl)
			                .build();
			        return result.toString();
				}
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");
			// ex.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JsonObject returnVal = Json.createObjectBuilder()
                .add("message", "no project found")
                .build();
        return returnVal.toString();
	}

}
