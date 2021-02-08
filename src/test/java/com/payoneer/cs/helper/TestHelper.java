package com.payoneer.cs.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import com.payoneer.cs.job.model.JobExecutionType;
import com.payoneer.cs.job.model.JobType;

@Component
public class TestHelper {
	private byte[] getFile() {
		try {
			return Files.readAllBytes(new ClassPathResource("jm-job-logger-1.0.jar").getFile().toPath());
		} catch (IOException e) {
			return null;
		}
	}

	public MultipartBodyBuilder submitJobRequestData() {
		return constructJobRequest(JobExecutionType.IMMEDIATE);
	}

	private MultipartBodyBuilder constructJobRequest(JobExecutionType jobExecutionType) {
		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();

		multipartBodyBuilder.part("file", getFile())
				.header("Content-Disposition", "form-data; name=file; filename=jm-job-logger-1.0.jar")
				.header("Content-Type", "multipart/form-data");
		multipartBodyBuilder.part("jobType", JobType.SPRING_BOOT_JAR.toString());
		multipartBodyBuilder.part("executionType", JobExecutionType.IMMEDIATE.toString());
		multipartBodyBuilder.part("jobGroupName", "TestGroupName");
		multipartBodyBuilder.part("jobName", "TestPostJob");
		multipartBodyBuilder.part("parameters", "TestPostJob");
		return multipartBodyBuilder;
	}

	public MultipartBodyBuilder submitScheduledJobRequestData() {
		MultipartBodyBuilder multipartBodyBuilder = constructJobRequest(JobExecutionType.SCHEDULED);
		multipartBodyBuilder.part("priority", 6);
		multipartBodyBuilder.part("schedule", getScheduleDateTime());
		return multipartBodyBuilder;
	}

	public MultipartBodyBuilder submitScheduledCronJobRequestData() {
		MultipartBodyBuilder multipartBodyBuilder = constructJobRequest(JobExecutionType.SCHEDULED);
		multipartBodyBuilder.part("priority", 6);
		multipartBodyBuilder.part("schedule", getScheduleDateTime());
		multipartBodyBuilder.part("cronExpression", "0 0 12 * * ?");
		return multipartBodyBuilder;
	}

	private String getScheduleDateTime() {
		int addMinuteTime = 5;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(DateUtils.addMinutes(new Date(), addMinuteTime));
	}

	public MultipartBodyBuilder submitJobRequestData_MissingJobFile() {
		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();

		multipartBodyBuilder.part("jobType", JobType.SPRING_BOOT_JAR.toString());
		multipartBodyBuilder.part("executionType", JobExecutionType.IMMEDIATE.toString());

		return multipartBodyBuilder;
	}

	public HttpEntity<LinkedMultiValueMap<String, Object>> getEntityForJobUpdate() {
		return getHttpEntity(constructUpdateJobParameters());
	}

	private HttpEntity<LinkedMultiValueMap<String, Object>> getHttpEntity(
			LinkedMultiValueMap<String, Object> parameters) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new HttpEntity<LinkedMultiValueMap<String, Object>>(parameters, headers);
	}

	public HttpEntity<LinkedMultiValueMap<String, Object>> getEntityForCronJobUpdate() {
		LinkedMultiValueMap<String, Object> parameters = constructUpdateJobParameters();
		parameters.add("schedule", "0 0 11 * * ?");
		return getHttpEntity(parameters);
	}

	private LinkedMultiValueMap<String, Object> constructUpdateJobParameters() {
		LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
		parameters.add("jobType", JobType.SPRING_BOOT_JAR.toString());
		parameters.add("jobGroupName", "TestGroupName");
		parameters.add("jobName", "TestPostJob");
		parameters.add("priority", 6);
		parameters.add("schedule", getScheduleDateTime());
		return parameters;
	}

}
