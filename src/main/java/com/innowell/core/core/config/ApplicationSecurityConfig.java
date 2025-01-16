package com.innowell.core.core.config;

import com.innowell.core.core.filters.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class ApplicationSecurityConfig {

    private static final String[] ALLOWED_PATHS = {"/innowell-mapper/admin/**", "/innowell-mapper/external-api-client/**", "/innowell-mapper/auth/**", "/auth/**", "/innowell-mapper/test/**", "/innowell-mapper/auth/login-urls/**", "/innowell-mapper/auth/token/login", "/innowell-mapper/auth/token/refresh", "/innowell-mapper/actuator/**", "/innowell-mapper/v1/verify/admin/**", "/innowell-mapper/internal/**", "/innowell-mapper/swagger-ui.html", "/innowell-mapper/v3/api-docs/**", "/innowell-mapper/swagger-ui/**", "/innowell-mapper/webjars/swagger-ui/**", "/innowell-mapper/api-docs/**", "/innowell-mapper/public/file-metadata/**"};
    @Autowired
    private JwtTokenFilter jwtAuthFilter; // Inject JwtAuthFilter dependency
    @Autowired
    private CorsConfigurationSource corsConfigurationSource;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors
                        .configurationSource(request -> {
                            CorsConfiguration config = new CorsConfiguration();
                            config.setAllowedOrigins(List.of("*"));
                            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                            config.setAllowedHeaders(List.of("*"));
                            return config;
                        })
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(ALLOWED_PATHS).permitAll()
                        .anyRequest().hasAnyRole("ADMIN", "PROJECT_ENGINEER", "VIEWER")
                )
                .exceptionHandling(exh -> exh
                        .authenticationEntryPoint((request, response, ex) ->
                                ResponseUtils.fail(response, "unauthorized")
                        )
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean for password encoder using BCrypt algorithm
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }


    // Bean to get AuthenticationManager from AuthenticationConfiguration
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
