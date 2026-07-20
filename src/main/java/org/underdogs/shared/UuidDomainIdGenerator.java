package org.underdogs.shared;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidDomainIdGenerator implements DomainIdGenerator {

  @Override
  public String generate() {
    return UUID.randomUUID().toString();
  }
}
