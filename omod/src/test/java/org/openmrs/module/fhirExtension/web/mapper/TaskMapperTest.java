package org.openmrs.module.fhirExtension.web.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.module.fhir2.model.FhirReference;
import org.openmrs.module.fhir2.model.FhirTask;
import org.openmrs.module.fhirExtension.model.FhirTaskRequestedPeriod;
import org.openmrs.module.fhirExtension.model.Task;
import org.openmrs.module.fhirExtension.web.contract.TaskFhirReference;
import org.openmrs.module.fhirExtension.web.contract.TaskRequest;
import org.openmrs.module.fhirExtension.web.contract.TaskResponse;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class TaskMapperTest {
	
	@Mock
	private EncounterService encounterService;
	
	@Mock
	private VisitService visitService;
	
	@Mock
	private PatientService patientService;
	
	@InjectMocks
	private TaskMapper taskMapper;
	
	private static final String OBS_UUID = "obs-uuid-1234";
	
	private static final String ORDER_UUID = "order-uuid-5678";
	
	@Test
	public void fromRequest_shouldSetFocusReference() {
		TaskRequest request = new TaskRequest();
		TaskFhirReference focus = new TaskFhirReference();
		focus.setType("Observation");
		focus.setReference("Observation/" + OBS_UUID);
		request.setFocus(focus);
		request.setIsSystemGeneratedTask(false);
		
		FhirReference result = taskMapper.fromRequest(request).getFhirTask().getFocusReference();
		
		assertNotNull(result);
		assertEquals("Observation", result.getType());
		assertEquals("Observation/" + OBS_UUID, result.getReference());
		assertEquals(OBS_UUID, result.getTargetUuid());
	}
	
	@Test
	public void fromRequest_shouldLeaveFocusNullWhenAbsent() {
		TaskRequest request = new TaskRequest();
		request.setIsSystemGeneratedTask(false);
		
		assertNull(taskMapper.fromRequest(request).getFhirTask().getFocusReference());
	}
	
	@Test
	public void fromRequest_shouldSetBasedOnReference() {
		TaskRequest request = new TaskRequest();
		TaskFhirReference basedOn = new TaskFhirReference();
		basedOn.setType("ServiceRequest");
		basedOn.setReference("ServiceRequest/" + ORDER_UUID);
		request.setBasedOn(basedOn);
		request.setIsSystemGeneratedTask(false);
		
		Set<FhirReference> refs = taskMapper.fromRequest(request).getFhirTask().getBasedOnReferences();
		
		assertNotNull(refs);
		assertEquals(1, refs.size());
		FhirReference result = refs.iterator().next();
		assertEquals("ServiceRequest", result.getType());
		assertEquals("ServiceRequest/" + ORDER_UUID, result.getReference());
		assertEquals(ORDER_UUID, result.getTargetUuid());
	}
	
	@Test
	public void constructResponse_shouldReturnFocusAndBasedOn() {
		FhirTask fhirTask = new FhirTask();
		FhirReference patientRef = new FhirReference();
		patientRef.setTargetUuid("visit-uuid");
		fhirTask.setForReference(patientRef);
		
		FhirReference focus = new FhirReference();
		focus.setType("Observation");
		focus.setReference("Observation/" + OBS_UUID);
		fhirTask.setFocusReference(focus);
		
		FhirReference basedOn = new FhirReference();
		basedOn.setType("ServiceRequest");
		basedOn.setReference("ServiceRequest/" + ORDER_UUID);
		Set<FhirReference> basedOnRefs = new HashSet<>();
		basedOnRefs.add(basedOn);
		fhirTask.setBasedOnReferences(basedOnRefs);
		
		FhirTaskRequestedPeriod period = new FhirTaskRequestedPeriod();
		period.setRequestedStartTime(new Date());
		period.setRequestedEndTime(new Date());
		
		TaskResponse response = taskMapper.constructResponse(new Task(fhirTask, period));
		
		assertNotNull(response.getFocus());
		assertEquals("Observation/" + OBS_UUID, response.getFocus().getReference());
		assertNotNull(response.getBasedOn());
		assertEquals("ServiceRequest/" + ORDER_UUID, response.getBasedOn().getReference());
	}
	
	@Test
	public void constructResponse_shouldLeaveReferencesNullWhenAbsent() {
		FhirTask fhirTask = new FhirTask();
		FhirReference patientRef = new FhirReference();
		patientRef.setTargetUuid("visit-uuid");
		fhirTask.setForReference(patientRef);
		
		FhirTaskRequestedPeriod period = new FhirTaskRequestedPeriod();
		TaskResponse response = taskMapper.constructResponse(new Task(fhirTask, period));
		
		assertNull(response.getFocus());
		assertNull(response.getBasedOn());
	}
}
