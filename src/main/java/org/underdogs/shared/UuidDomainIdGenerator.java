package org.underdogs.shared;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidDomainIdGenerator implements DomainIdGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
