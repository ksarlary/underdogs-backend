package org.underdogs.bets.infrastructure.sqs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.underdogs.bets.application.services.BetResolutionService;
import org.underdogs.bets.infrastructure.sqs.BetResolutionSqsListener.SqsListenerException;
import org.underdogs.bets.infrastructure.sqs.dto.BetResolutionResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du listener SQS pour la résolution des paris")
class BetResolutionSqsListenerTest {

  @Mock private BetResolutionService betResolutionService;

  private BetResolutionSqsListener listener;
  private ObjectMapper objectMapper;

  private Resource successJson;
  private Resource failureJson;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    listener = new BetResolutionSqsListener(betResolutionService, objectMapper);

    successJson = new ClassPathResource("sqs-payloads/response_success.json");
    failureJson = new ClassPathResource("sqs-payloads/response_failure.json");
  }

  @Test
  @DisplayName("Devrait traiter avec succès une résolution de paris positive")
  void shouldProcessSuccessfulResolution() throws Exception {
    String successPayload = readResourceAsString(successJson);

    listener.handleBetResolutionMessage(successPayload);

    ArgumentCaptor<BetResolutionResponse> captor =
        ArgumentCaptor.forClass(BetResolutionResponse.class);
    verify(betResolutionService).processBetResolution(captor.capture());

    BetResolutionResponse capturedResponse = captor.getValue();
    assertThat(capturedResponse).isNotNull();
    assertThat(capturedResponse.correlationId()).isEqualTo("corr-123-success");
    assertThat(capturedResponse.status()).isEqualTo("success");
    assertThat(capturedResponse.errorMessage()).isNull();

    assertThat(capturedResponse.results()).isNotNull().hasSize(2);

    BetResolutionResponse.BetResult firstResult = capturedResponse.results().get(0);
    assertThat(firstResult.betId()).isEqualTo("bet-1");
    assertThat(firstResult.matchId()).isEqualTo("match-1");

    BetResolutionResponse.BetResult secondResult = capturedResponse.results().get(1);
    assertThat(secondResult.betId()).isEqualTo("bet-2");
    assertThat(secondResult.matchId()).isEqualTo("match-1");
  }

  @Test
  @DisplayName("Devrait gérer une résolution de paris échouée sans crasher l'application")
  void shouldHandleFailedResolution() throws Exception {
    String failurePayload = readResourceAsString(failureJson);

    listener.handleBetResolutionMessage(failurePayload);

    ArgumentCaptor<BetResolutionResponse> captor =
        ArgumentCaptor.forClass(BetResolutionResponse.class);
    verify(betResolutionService).processBetResolution(captor.capture());

    BetResolutionResponse capturedResponse = captor.getValue();
    assertThat(capturedResponse).isNotNull();
    assertThat(capturedResponse.correlationId()).isEqualTo("corr-456-failure");
    assertThat(capturedResponse.status()).isEqualTo("failure");
    assertThat(capturedResponse.errorMessage())
        .isEqualTo("Failed to resolve bets: Database connection timeout");

    assertThat(capturedResponse.results()).isEmpty();
  }

  @Test
  @DisplayName("Devrait lever une exception quand le JSON est invalide")
  void shouldThrowExceptionWhenJsonIsInvalid() {
    String invalidJson = "{ invalid json }";

    assertThatThrownBy(() -> listener.handleBetResolutionMessage(invalidJson))
        .isInstanceOf(SqsListenerException.class)
        .hasMessageContaining("Impossible de traiter le message SQS")
        .hasCauseInstanceOf(IOException.class);

    verifyNoInteractions(betResolutionService);
  }

  @Test
  @DisplayName("Devrait lever une exception quand le JSON est null")
  void shouldThrowExceptionWhenJsonIsNull() {
    assertThatThrownBy(() -> listener.handleBetResolutionMessage(null))
        .isInstanceOf(SqsListenerException.class)
        .hasMessageContaining("Impossible de traiter le message SQS");

    verifyNoInteractions(betResolutionService);
  }

  private String readResourceAsString(Resource resource) throws IOException {
    return new String(resource.getInputStream().readAllBytes());
  }
}
