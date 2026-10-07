package org.openmrs.module.fhirExtension.web.contract;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskInputResponseDTO {
	
	private Object type;
	
	private String valueText;
}
