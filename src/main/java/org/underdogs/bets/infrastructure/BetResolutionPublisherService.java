package org.underdogs.bets.infrastructure;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.underdogs.bets.dtos.ResolveEventBetsRequest;

@Service
public class BetResolutionPublisherService {

  @Value("${sqs.queues.request-queue-name}")
  private String requestQueueName;

  private final SqsTemplate sqsTemplate;

  @Autowired
  public BetResolutionPublisherService(SqsTemplate sqsTemplate) {
    this.sqsTemplate = sqsTemplate;
  }

  public void publishBetResolutionRequest(ResolveEventBetsRequest request) {
    sqsTemplate.send(to -> to.queue(requestQueueName).payload(request));
  }
}
