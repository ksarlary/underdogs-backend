package org.underdogs.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public class KeycloakJwtRolesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Set<String> roles = new HashSet<>();

        Object realmAccess = jwt.getClaims().get("realm_access");
        if (realmAccess instanceof Map<?, ?> realmAccessMap) {
            Object realmRoles = realmAccessMap.get("roles");
            if (realmRoles instanceof Collection<?> collection) {
                collection.stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .forEach(roles::add);
            }
        }

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}