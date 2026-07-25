package org.underdogs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http, BlockedUserFilter blockedUserFilter) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/actuator/health", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/teams/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/players/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/tournaments/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/matches/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v2/matches/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/test/bets/**")
                    .permitAll()
                    .requestMatchers("/api/v1/teams", "/api/v1/teams/*")
                    .authenticated()
                    .requestMatchers("/api/v1/players", "/api/v1/players/*")
                    .authenticated()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
        .addFilterAfter(blockedUserFilter, BearerTokenAuthenticationFilter.class);

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

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));

    configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));

    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
