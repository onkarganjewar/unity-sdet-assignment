package com.unity3d.project.demo;

import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unity3d.project.controller.ProjectController;
import com.unity3d.project.model.KeysWrapper;
import com.unity3d.project.model.Project;

import static org.hamcrest.CoreMatchers.containsString;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.json.Json;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SdetProjectApplication.class })
@WebAppConfiguration
@SpringBootTest
public class SdetProjectApplicationTests {

	@Autowired
	private ObjectMapper objectMapper;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	/** Logger **/
	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

	/** Default error response in json string */
	private final static String jsonErrorResponse = Json.createObjectBuilder().add("message", "no project found")
			.build().toString();

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				./* addFilter(new GlobalFilter()) */
				build();

		// Clear and store sample projects in the "Projects.txt" every time the
		// server is restarted
		initializeProjects();

	}

	/**
	 * Initializes and clears(if file contains any records) "Projects.txt" file
	 * with sample records
	 */
	private void initializeProjects() {

		// Create a list of sample projects
		List<Project> projects = createDummyProjects();

		// File to write/read project records
		FileWriter file = null;

		try {
			file = new FileWriter("Projects.txt");
			// empty the current content
			file.write("");

			// Save the created projects in "Projects.txt"
			for (Project p : projects) {
				// Convert the POJO into a json string format
				Gson gson = new GsonBuilder().create();
				String projectJson = gson.toJson(p);
				file.write(projectJson);

				// insert a line separator after each project insert
				String newline = System.getProperty("line.separator");
				file.append(newline);

				// Log the project details
				logger.info("Wrote record = " + p);
			}
		} catch (IOException e) {
			logger.debug("Error reading file '" + file + "'");
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException ex) {
					logger.error("Unable to write records in " + file + " file.... Due to exception = "
							+ ex.getLocalizedMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Test to check whether createProject throws an exception when different
	 * type of object is passed as payload
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateProjectThrowsException() throws Exception {
		// String sampleJsonString = String.format("{\"name\": \"String
		// 1\",\"description\": \"Description\"}");

		// Create a dummy class for sending invalid object to /createproject
		class Student {
			String firstName;
			String password;
			public Student(String firstName, String password) {
				super();
				this.firstName = firstName;
				this.password = password;
			}
		}

		String expectedErrorSubstring = "No serializer found for class com.unity3d.project.demo.SdetProjectApplicationTests";
		// String expectedErrorSubstring = " no properties discovered to create BeanSerializer ";
		Student s = new Student("Onkar", "password");

		// arrange/setup --- expect an exception of type jackson.databind.
		// since @RequestBody is expecting an object of type "Project" 
		exception.expect(JsonMappingException.class);
		exception.expectMessage(containsString(expectedErrorSubstring));

		// action/followup
		this.mockMvc.perform(post("/createproject").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(s))).andDo(print());
	}


	/**
	 * Test to check incomplete or partial '/createproject' request like empty or null projectName/projectId/etc.
	 * @throws Exception
	 */
	@Test
	public void testCreateProjectNullRequest() throws Exception {
//		String jsonRes = Json.createObjectBuilder().add("message", "Please input valid projectid AND projectCost AND projectName!!!")
//				.build().toString();
		
		Project project = new Project(0, "Project Name", true, null, null,
				null, 22.245, null, Arrays.asList(new KeysWrapper(25, "movie"), new KeysWrapper(30, "sports")));

		// Expect an HTTP 400 BAD REQUEST error
		this.mockMvc
				.perform(post("/createproject").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(project)))
				.andDo(print()).andExpect(status().is4xxClientError()).andDo(print());
//				.andExpect(content().string(jsonRes)).andDo(print());
	}

	
	
	/**
	 * Test to check whether there's any error when user sends invalid format
	 * data to server
	 */
	@Test
	public void testInvalidCreateProjectPayload() throws Exception {
		// Expect an HTTP 400 BAD REQUEST error
		this.mockMvc.perform(post("/createproject").contentType(MediaType.APPLICATION_JSON).param("firstName", "onkar")
				.param("lastName", "ganjewar")).andExpect(status().is4xxClientError()).andDo(print());
	}

	/**
	 * Test to check whether the project is created successfully and whether it
	 * returns a correct message or not
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateProject() throws Exception {

		Project project = new Project(7, "test project number 7", true, "12112010 00:12:22", "11082020 22:21:11",
				Arrays.asList("IRELAND", "INDIA", "USA"), 22.23, "http://www.test.com",
				Arrays.asList(new KeysWrapper(25, "movie"), new KeysWrapper(35, "games")));

		this.mockMvc
				.perform(post("/createproject").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(project)))
				.andDo(print()).andExpect(status().is2xxSuccessful())
				.andExpect(content().string("campaign is successfully created"));
	}

	/**
	 * Test to check whether '/requestproject' API works accordingly
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRequestProject_AllParameters() throws Exception {

		// Project with Id=1 is expired
		this.mockMvc
				.perform(get("/requestproject?projectid=1&country=usa&number=29&keyword=sports")
						.accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print()).andExpect(status().is4xxClientError()).andDo(print())
				.andExpect(content().string(jsonErrorResponse)).andDo(print());

	}

	/**
	 * Test to get the project details by ID
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRequestProject_OnlyId() throws Exception {

		String jsonResponse = Json.createObjectBuilder().add("projectName", "game project 4").add("projectCost", 15.5)
				.add("projectUrl", "http://www.unity3d.com").build().toString();

		this.mockMvc.perform(get("/requestproject?projectid=4").accept(MediaType.APPLICATION_JSON_UTF8)).andDo(print())
				.andExpect(status().is2xxSuccessful()).andDo(print()).andExpect(content().string(jsonResponse))
				.andDo(print());
	}

	/**
	 * Test to get the project details by providing only target country name
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRequestProject_OnlyCountry() throws Exception {

		String jsonResponse = Json.createObjectBuilder().add("projectName", "game project 4").add("projectCost", 15.5)
				.add("projectUrl", "http://www.unity3d.com").build().toString();
		this.mockMvc.perform(get("/requestproject?country=ireland").accept(MediaType.APPLICATION_JSON_UTF8))
				.andDo(print()).andExpect(status().is2xxSuccessful()).andDo(print())
				.andExpect(content().string(jsonResponse)).andDo(print());

	}

	/**
	 * Test to get the project details by targetKeys--name AND
	 * targetCountries--name
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRequestProject_Country_Number() throws Exception {

		// All the projects having "usa" in their target countries are expired
		this.mockMvc.perform(get("/requestproject?country=usa&number=20").accept(MediaType.ALL)).andDo(print())
				.andDo(print()).andExpect(status().is4xxClientError()).andDo(print())
				.andExpect(content().string(jsonErrorResponse)).andDo(print());
	}

	/**
	 * Test to get the highest cost project when no URL parameters are provided
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRequestProject_NoParam() throws Exception {

		String jsonResponse = Json.createObjectBuilder().add("projectName", "game project 4").add("projectCost", 15.5)
				.add("projectUrl", "http://www.unity3d.com").build().toString();
		this.mockMvc.perform(get("/requestproject").accept(MediaType.APPLICATION_JSON_UTF8)).andDo(print())
				.andExpect(status().is2xxSuccessful()).andDo(print()).andExpect(content().string(jsonResponse))
				.andDo(print());
	}

	/**
	 * Dummy test for hello world
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSayHelloWorld() throws Exception {
//		.content("{\"userName\":\"testUserDetails\",\"firstName\":\"xxx\",\"lastName\":\"xxx\",\"password\":\"xxx\"}"))
		this.mockMvc.perform(get("/hello").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8")).andDo(print());
	}

	private List<Project> createDummyProjects() {

		// Project project = initializeProject(id, name, enabled, creationDate,
		// expDate, )

		Project p1 = new Project(1, "test project number 1", true, "05112017 00:00:00", "05212017 00:00:00",
				Arrays.asList("USA", "CANADA", "MEXICO", "BRAZIL"), 5.5, "http://www.unity3d.com",
				Arrays.asList(new KeysWrapper(25, "movie"), new KeysWrapper(30, "sports")));

		Project p2 = new Project(2, "test project number 2", true, "05112017 00:00:00", "05202017 00:00:00",
				Arrays.asList("USA", "CANADA"), 2.5, "http://www.google.com",
				Arrays.asList(new KeysWrapper(25, "movie"), new KeysWrapper(30, "sports")));

		Project p3 = new Project(3, "test project number 3", true, "03132017 00:00:00", "02202019 00:00:00",
				Arrays.asList("CANADA", "ENGLAND", "INDIA"), 0.5, "http://www.unity3d.com",
				Arrays.asList(new KeysWrapper(20, "cars")));

		Project p4 = new Project(4, "game project 4", true, "01292017 00:00:00", "09222017 00:00:00",
				Arrays.asList("IRELAND", "FINLAND"), 15.5, "http://www.unity3d.com",
				Arrays.asList(new KeysWrapper(11, "games"), new KeysWrapper(10, "mobile")));

		List<Project> projects = new ArrayList<Project>();
		projects.add(p1);
		projects.add(p2);
		projects.add(p3);
		projects.add(p4);

		return projects;
	}

}
