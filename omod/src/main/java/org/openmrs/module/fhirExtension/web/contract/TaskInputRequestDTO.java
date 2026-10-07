package org.openmrs.module.fhirExtension.web.contract;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskInputRequestDTO {
	
	private String typeUuid;
	
	private String valueText;
}
