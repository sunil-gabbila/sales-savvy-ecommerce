
package com.kodnest.salessavvy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // Allow anyone to register
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/users/register"
                ).permitAll()

                // Allow anyone to log in
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/auth/login"
                ).permitAll()

                // Protect all remaining endpoints
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
