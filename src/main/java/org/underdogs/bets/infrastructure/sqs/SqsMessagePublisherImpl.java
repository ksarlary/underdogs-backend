package org.underdogs.bets.infrastructure.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SqsMessagePublisherImpl implements SqsMessagePublisher {

  private static final Logger logger = LoggerFactory.getLogger(SqsMessagePublisherImpl.class);

  private final ObjectMapper objectMapper;

  public SqsMessagePublisherImpl(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void sendMessage(String queueName, Object payload) {
    try {
      String jsonPayload = objectMapper.writeValueAsString(payload);
      logger.debug("Message envoyé à la file {}: {}", queueName, jsonPayload);
    } catch (Exception e) {
      logger.error("Erreur lors de la sérialisation du message pour la file {}", queueName, e);
      throw new RuntimeException("Impossible d'envoyer le message à SQS", e);
    }
  }
}
