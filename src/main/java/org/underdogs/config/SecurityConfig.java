package org.underdogs.config;

import java.util.ArrayList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth -> auth
                    // OpenAPI & health public routes
                    .requestMatchers(
                            "/actuator/health",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**"
                    ).permitAll()
                    // Public read-only routes
                    .requestMatchers(HttpMethod.GET, "/api/v1/teams/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/players/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/tournaments/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/matches/**").permitAll()

                    .requestMatchers("/api/v1/teams", "/api/v1/teams/*")
                    .authenticated()
                    .requestMatchers("/api/v1/players", "/api/v1/players/*")
                    .authenticated()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

    return http.build();
  }

  @Bean
  public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter defaultScopesConverter = new JwtGrantedAuthoritiesConverter();
    KeycloakJwtRolesConverter keycloakRolesConverter = new KeycloakJwtRolesConverter();

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
        jwt -> {
          var authorities = new ArrayList<>(defaultScopesConverter.convert(jwt));
          authorities.addAll(keycloakRolesConverter.convert(jwt));
          return authorities;
        });

    return jwtAuthenticationConverter;
  }
}
