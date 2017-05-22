package com.unity3d.project.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.unity3d.project.model.Project;


@Component
public class ProjectValidator implements Validator {

	@Override
	public boolean supports(Class<?> arg0) {
		return Project.class.equals(arg0);
	}

	@Override
	public void validate(Object target, Errors e) {

        ValidationUtils.rejectIfEmptyOrWhitespace(e, "projectName", null, "Project name is mandatory");
//        ValidationUtils.rejectIfEmpty(e, "id", "Project Id is Mandatory");
//        ValidationUtils.rejectIfEmpty(e, "expiryDate ", "Expiry Date is mandatory");
//        ValidationUtils.rejectIfEmpty(e, "targetCountries", "message.countries", "Project target countries is mandatory");
        Project p = (Project) target;

        if (p.getId() <= 0) {
        	e.rejectValue(null, null, "Project ID is not valid");
        }

        if (p.getProjectCost() <= 0) {
        	e.rejectValue(null, null, "Project Cost is not valid");
        }
	}
}
