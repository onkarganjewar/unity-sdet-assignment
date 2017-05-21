package com.unity3d.project.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unity3d.project.model.KeysWrapper;
import com.unity3d.project.model.Project;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SdetProjectApplication.class })
@WebAppConfiguration
public class SdetProjectApplicationTests {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Before
	public void createMockMVC() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				./* addFilter(new GlobalFilter()) */
				build();
	}

	/**
	 * Test to check whether the project is created successfully and whether it returns a correct message or not
	 * @throws Exception
	 */
	@Test
	public void testCreateProject() throws Exception {
		// String[] countries = new String[]{"USA", "INDIA"};

		List<Project> projects = createDummyProjects();

		// Project project = new Project(233, "ASDASD", true, "01012010
		// 00:00:00", "04042040 00:00:00", countriesList, 123.213,
		// "www.sample.com", keysList);

		this.mockMvc
				.perform(post("/createproject").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(projects.get(0))))
				.andDo(print()).andExpect(status().is2xxSuccessful())
				.andExpect(content().string("campaign is successfully created"));
	}

	/**
	 * Test to check whether '/requestproject' API works accordingly
	 * @throws Exception
	 */
	@Test
	public void testRequestProject_AllParameters() throws Exception {

		String json = Json.createObjectBuilder()
	            .add("projectName", "test project number 1")
	            .add("projectCost", 5.5)
	            .add("projectUrl", "http://www.unity3d.com")
	            .build()
	            .toString();
		this.mockMvc
				.perform(get("/requestproject?projectid=1&country=usa&number=29&keyword=sports").accept(MediaType.ALL))
				.andDo(print())
				.andExpect(status().is2xxSuccessful())
				.andDo(print())		
				.andExpect(content().string(json)).andDo(print());
//				.andReturn()

//		String content = result.getResponse().getContentAsString();
//		System.out.println(content);
	}

	@Test
	public void testRequestProject_OnlyId() throws Exception {

		String json = Json.createObjectBuilder()
	            .add("projectName", "test project number 1")
	            .add("projectCost", 5.5)
	            .add("projectUrl", "http://www.unity3d.com")
	            .build()
	            .toString();
		this.mockMvc
				.perform(get("/requestproject?projectid=1").accept(MediaType.ALL))
				.andDo(print())
				.andExpect(status().is2xxSuccessful())
				.andDo(print())		
				.andExpect(content().string(json)).andDo(print());
	}


	@Test
	public void testRequestProject_OnlyCountry() throws Exception {

		String json = Json.createObjectBuilder()
	            .add("projectName", "test project number 6")
	            .add("projectCost", 0.5)
	            .add("projectUrl", "http://www.unity3d.com")
	            .build()
	            .toString();
		this.mockMvc
				.perform(get("/requestproject?country=usa").accept(MediaType.ALL))
				.andDo(print())
				.andExpect(status().is2xxSuccessful())
				.andDo(print())		
				.andExpect(content().string(json)).andDo(print());
	}

	@Test
	public void testRequestProject_Country_Number() throws Exception {

		String json = Json.createObjectBuilder()
	            .add("projectName", "test project number 1")
	            .add("projectCost", 0.5)
	            .add("projectUrl", "http://www.unity3d.com")
	            .build()
	            .toString();
		this.mockMvc
				.perform(get("/requestproject?country=usa&number=20").accept(MediaType.ALL))
				.andDo(print())
				.andExpect(status().is2xxSuccessful())
				.andDo(print())		
				.andExpect(content().string(json)).andDo(print());
	}

			
			
	@Test
	public void testRequestProject_NoParam() throws Exception {

		String json = Json.createObjectBuilder()
	            .add("projectName", "test project number 3")
	            .add("projectCost", 222.5)
	            .add("projectUrl", "http://www.unity3d.com")
	            .build()
	            .toString();
		this.mockMvc
				.perform(get("/requestproject").accept(MediaType.ALL))
				.andDo(print())
				.andExpect(status().is2xxSuccessful())
				.andDo(print())		
				.andExpect(content().string(json)).andDo(print());
	}
	
	@Test
	public void testSayHelloWorld() throws Exception {
		this.mockMvc.perform(get("/hello").accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8")).andDo(print());		
	}

	private List<Project> createDummyProjects() {
		List<Project> projects = new ArrayList<Project>();

		String country1 = "USA";
		String country2 = "INDIA";
		String country3 = "IRELAND";

		List<String> countriesList = new ArrayList<String>();
		countriesList.add(country1);
		countriesList.add(country2);
		countriesList.add(country3);

		KeysWrapper keys = new KeysWrapper();
		keys.setKeyword("sports");
		keys.setNumber(20);

		KeysWrapper keys2 = new KeysWrapper();
		keys2.setKeyword("movie");
		keys2.setNumber(25);

		KeysWrapper keys3 = new KeysWrapper();
		keys2.setKeyword("games");
		keys2.setNumber(35);

		List<KeysWrapper> keysList = new ArrayList<KeysWrapper>();
		keysList.add(keys);
		keysList.add(keys2);
		keysList.add(keys3);

		Double projectCost = 22.23;

		Project p1 = new Project(7, "test project 7", true, "12112010 00:12:22", "11082020 22:21:11", countriesList,
				projectCost, "www.test.com", keysList);

		projects.add(p1);

		return projects;
	}
}
