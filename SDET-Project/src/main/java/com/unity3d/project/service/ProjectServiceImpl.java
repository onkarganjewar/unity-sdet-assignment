package com.unity3d.project.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.unity3d.project.controller.ProjectController;
import com.unity3d.project.exception.ProjectException;
import com.unity3d.project.model.KeysWrapper;
import com.unity3d.project.model.Project;

@Service("ProjectService")
public class ProjectServiceImpl implements ProjectService {

	/** Logger **/
	private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

	/** Database input file **/
	private static final String dataFile = "Projects.txt";

	@Override
	public Project getProjectById(Long id) throws ProjectException {

		// This will reference one line at a time
		String line = null;

		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(dataFile);
			@SuppressWarnings("resource")
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				Project project = new Gson().fromJson(line, Project.class);
				if (project.getId() == id) {

					// Service should always return projects which are enabled,
					if (project.isEnabled() == false) {
						throw new ProjectException("Project is not enabled. Cannot retrieve the project.");
					}

					// Service should never return a project if projectUrl is null
					if (project.getProjectUrl() == null) {
						throw new ProjectException("Project URL is empty. Cannot retrieve the project.");
					}

					if (project.getExpiryDate() == null) {
						throw new ProjectException("Project expiry date is undefined. Cannot retrieve the project.");
					}

					// Service should never return a project which is expired
					if (!checkExpiryDate(project.getExpiryDate())) {
						throw new ProjectException("Sorry. Project is expired");
					}
					return project;
				}
				logger.info(project.toString());
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			logger.error(ex.getMessage());
			logger.debug("Unable to open file '" + dataFile + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + dataFile + "'");
			logger.error(ex.getClass().getCanonicalName());
		}
		return null;
	}

	@Override
	public List<Project> getProjectListByCountry(String country) throws ProjectException {

		/** Stores a list of project records **/
		List<Project> projectsList = new ArrayList<Project>();
		String line = null;
		try {
			FileReader fileReader = new FileReader(dataFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				Project project = new Gson().fromJson(line, Project.class);
				logger.info("Project found =  " + project.toString());

				// Service should always return projects which are
				// enabled,
				if (project.isEnabled() == false) {
					continue;
				}

				// Service should never return a project if projectUrl
				// is null
				if (project.getProjectUrl() == null) {
					continue;
				}

				// Service should never return a project which is
				// expired
				if (project.getExpiryDate() == null) {
					continue;
				}

				// Service should never return a project which is
				// expired
				if (!checkExpiryDate(project.getExpiryDate())) {
					continue;
				}
				List<String> countries = project.getTargetCountries();
				for (String s : countries) {
					if (s.equalsIgnoreCase(country)) {
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
			logger.error(ex.getStackTrace().toString());// ex.printStackTrace();
		}

		if (projectsList.isEmpty()) {
			String classMethodName = this.getClass().getName()+"."+ Thread.currentThread().getStackTrace()[1].getMethodName();
			String errorMessage = "Record matching all the criteria not found with given target country name";
			logger.error("Exception occurred in "+ classMethodName + " due to "+errorMessage);
			throw new ProjectException(errorMessage);
		}
		return projectsList;

	}

	@Override
	public List<Project> getAllProjects() throws IllegalArgumentException, ProjectException {

		/** Stores a list of project records **/
		List<Project> projectsList = new ArrayList<Project>();

		String line = null;
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(dataFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				Project project = new Gson().fromJson(line, Project.class);
				// Service should always return projects which are enabled,
				if (project.isEnabled() == false) {
					continue;
				}

				// Service should never return a project if projectUrl is null
				else if (project.getProjectUrl() == null) {
					continue;
				}

				// Service should never return a project which is expired
				else if (project.getExpiryDate() == null) {
					continue;
				}

				// Service should never return a project which is expired
				else if (!checkExpiryDate(project.getExpiryDate())) {
					continue;
				} else
					projectsList.add(project);
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			logger.error(ex.getMessage());
			logger.debug("Unable to open file '" + dataFile + "'");
		} catch (IOException ex) {
			logger.error("Error reading file '" + dataFile + "'");
			logger.error("Exception occurred --- "+ex.getClass().getName());// ex.printStackTrace();
		}

		if (projectsList.isEmpty()) {
			String classMethodName = this.getClass().getName()+"."+ Thread.currentThread().getStackTrace()[1].getMethodName();
			String errorMessage = "There's no data in the input file....!!!!";
			logger.error("Exception occurred in "+ classMethodName + " due to "+errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		return projectsList;
	}

	@Override
	public List<Project> getProjectByKeyword(String keyword) throws ProjectException {
		/** Stores a list of project records **/
		List<Project> projectsList = new ArrayList<Project>();

		String line = null;
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(dataFile);
			@SuppressWarnings("resource")
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				Project project = new Gson().fromJson(line, Project.class);
				// Service should never return a project if projectUrl is null
				// Service should always return projects which are enabled,
				if (project.isEnabled() == false || project.getProjectUrl() == null || project.getExpiryDate() == null)
					continue;

				// Service should never return a project which is expired
				if (!checkExpiryDate(project.getExpiryDate()))
					continue;
				logger.info(project.toString());

				List<KeysWrapper> keyset = project.getTargetKeys();
				for (KeysWrapper k : keyset) {
					if (k.getKeyword().equalsIgnoreCase(keyword)) {
						// Service should always return projects which are
						// enabled,
						if (project.isEnabled() == false) {
							throw new ProjectException("Project is not enabled. Cannot retrieve the project.");
						}

						// Service should never return a project if projectUrl
						// is null
						else if (project.getProjectUrl() == null) {
							throw new ProjectException("Project URL is empty. Cannot retrieve the project.");
						}

						// Service should never return a project which is
						// expired
						else if (project.getExpiryDate() == null) {
							throw new ProjectException(
									"Project expiry date is undefined. Cannot retrieve the project.");
						}

						// Service should never return a project which is
						// expired
						else if (!checkExpiryDate(project.getExpiryDate())) {
							throw new ProjectException("Sorry. Project is expired");
						} else
							projectsList.add(project);
					}
				}
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			logger.error(ex.getMessage());
			logger.debug("Unable to open file '" + dataFile + "'");
		} catch (IOException ex) {
			logger.error("Error reading file '" + dataFile + "'");
			logger.error(ex.getClass().getCanonicalName());// ex.printStackTrace();
		}

		if (projectsList.isEmpty())
			return null;
		return projectsList;
	}

	@Override
	public List<Project> filterProjectListByNumberMin(List<Project> projects, double number) {
		/** Stores a list of project records **/
		List<Project> projectsList = new ArrayList<Project>();

		for (Project p : projects) {
			List<KeysWrapper> keyset = p.getTargetKeys();
			for (KeysWrapper k : keyset) {
				if (k.getNumber() >= number) {
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
		// Service should never return a project which is expired
//		String expDate = "Expiry Date = " + day + "/" + month + "/" + year;

		Calendar myCal = Calendar.getInstance();
		Date currDate = myCal.getTime();
		myCal.set(Calendar.YEAR, year);
		myCal.set(Calendar.MONTH, month - 1);
		myCal.set(Calendar.DAY_OF_MONTH, day);
		myCal.set(year, month - 1, day, hourOfDay, minute, second);
		Date expiry = myCal.getTime();

		// If today’s date is above expiry date then project should not be
		// selected.
		if (currDate.after(expiry)) {
			return false;
		}
		return true;
	}

}
