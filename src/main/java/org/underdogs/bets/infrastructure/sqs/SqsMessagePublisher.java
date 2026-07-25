package org.underdogs.bets.infrastructure.sqs;

public interface SqsMessagePublisher {
  void sendMessage(String queueName, Object payload);
}
