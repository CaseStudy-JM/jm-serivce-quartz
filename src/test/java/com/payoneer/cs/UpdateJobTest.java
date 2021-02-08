package com.payoneer.cs;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.payoneer.cs.helper.TestHelper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@AutoConfigureWebTestClient
class UpdateJobTest {

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private TestHelper testHelper;

	private CompletableFuture<String> completableFuture = new CompletableFuture<String>();
	private String jobId;

	@BeforeEach
	void setup() throws ExecutionException, InterruptedException {
		webTestClient.post().uri("/jms/api/v1/job")
				.body(BodyInserters.fromMultipartData(testHelper.submitScheduledJobRequestData().build())).exchange()
				.expectStatus().isCreated().expectHeader()
				.value("location", location -> completableFuture.complete(location));

		jobId = StringUtils.substringAfter(completableFuture.get(), "/jms/api/v1/job/");
	}

	@Test
	void testUpdateJob() throws ExecutionException, InterruptedException {
		webTestClient.mutate().responseTimeout(Duration.ofSeconds(30)).build().put()
				.uri("/jms/api/v1/job/".concat(jobId)).body(BodyInserters.fromValue(testHelper.getEntityForJobUpdate()))
				.exchange().expectStatus().isOk().returnResult(Boolean.class);
	}

}
