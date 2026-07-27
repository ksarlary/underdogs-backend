package org.underdogs.bets;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.underdogs.bets.dtos.ResolveEventBetsRequest;
import org.underdogs.bets.dtos.ResolveEventBetsResponse;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
class BetResolutionIntegrationTest {

    @Container
    static LocalStackContainer localStack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.4.0"))
                    .withServices(LocalStackContainer.Service.SQS);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.region", localStack::getRegion);
        registry.add("aws.credentials.access-key", localStack::getAccessKey);
        registry.add("aws.credentials.secret-key", localStack::getSecretKey);
        registry.add("spring.cloud.aws.sqs.endpoint", () -> localStack.getEndpointOverride(LocalStackContainer.Service.SQS).toString());
    }

    @Autowired
    private SqsTemplate sqsTemplate;

    @Autowired
    private BetResolutionPublisherService publisherService;

    @Test
    void happyPathTest() {
        // Given
        UUID correlationId = UUID.randomUUID();
        ResolveEventBetsRequest request = new ResolveEventBetsRequest(
                correlationId,
                "RESOLVE_EVENT_BETS",
                new ResolveEventBetsRequest.Payload(
                        "evt_98765",
                        "team_A",
                        Collections.singletonList(new ResolveEventBetsRequest.Bet(
                                "bet_001",
                                "usr_555",
                                "team_A",
                                100,
                                1.5
                        ))
                )
        );

        // When
        publisherService.publishBetResolutionRequest(request);

        // Then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            ResolveEventBetsResponse response = sqsTemplate.receive(from -> from.queue("underdogs-responses-queue"), ResolveEventBetsResponse.class);
            assertNotNull(response);
            assertEquals(correlationId, response.correlationId());
            assertEquals("SUCCESS", response.status());
            assertEquals(1, response.results().size());
            assertEquals("WON", response.results().get(0).status());
            assertEquals(150, response.results().get(0).kibblesToCredit());
        });
    }
}