package com.skodin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skodin.DTOs.ErrorDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityFilterChainConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(
                                        "/actuator/**",
                                        "/error",
                                        "/v3/**",
                                        "/api/auth/authenticate",
                                        "/swagger-ui/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(((request, response, authException) ->
                        {
                            response.setContentType("application/json");
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            ErrorDTO errorResponse = new ErrorDTO(HttpStatus.UNAUTHORIZED, "Incorrect data");
                            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
                        }))
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            ErrorDTO errorResponse = new ErrorDTO(HttpStatus.FORBIDDEN, "Access Denied");
                            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
                        })
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
