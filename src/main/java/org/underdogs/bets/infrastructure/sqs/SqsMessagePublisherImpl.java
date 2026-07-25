package org.underdogs.bets.infrastructure.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
public class SqsMessagePublisherImpl implements SqsMessagePublisher {

  private static final Logger logger = LoggerFactory.getLogger(SqsMessagePublisherImpl.class);

  private final ObjectMapper objectMapper;
  private final SqsClient sqsClient;
  private final Map<String, String> queueUrls = new ConcurrentHashMap<>();

  public SqsMessagePublisherImpl(ObjectMapper objectMapper, SqsClient sqsClient) {
    this.objectMapper = objectMapper;
    this.sqsClient = sqsClient;
  }

  @Override
  public void sendMessage(String queueName, Object payload) {
    try {
      String jsonPayload = objectMapper.writeValueAsString(payload);
      String queueUrl = queueUrls.computeIfAbsent(queueName, this::resolveQueueUrl);

      sqsClient.sendMessage(
          SendMessageRequest.builder().queueUrl(queueUrl).messageBody(jsonPayload).build());

      logger.info("Message envoyé à la file SQS {}", queueName);
    } catch (Exception e) {
      logger.error("Erreur lors de l'envoi du message vers la file SQS {}", queueName, e);
      throw new RuntimeException("Impossible d'envoyer le message à SQS", e);
    }
  }

  private String resolveQueueUrl(String queueName) {
    return sqsClient
        .getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build())
        .queueUrl();
  }
}
