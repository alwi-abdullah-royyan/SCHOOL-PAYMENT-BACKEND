package com.beta.schoolpayment.config;
import com.beta.schoolpayment.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
    private final JwtRequestFilter jwtRequestFilter;
    private final UserService userService;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, UserService userService) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Swagger and API Docs
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/swagger-ui/index.html").permitAll()
                        //public
                        .requestMatchers(HttpMethod.POST,"/api/users/login", "/api/users/register").permitAll()
                        //admin authenticated
                        .requestMatchers(HttpMethod.GET, "/api/users/filter", "/api/users", "/api/students",
                                "/api/students/search", "/api/school-years/search","/api/school-years/all",
                                "/api/payments","/api/classes/search","/api/classes/all","/api/students/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/students", "/api/school-years/create",
                                "/api/classes/create").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/users/role/{id}", "/api/students/{id}",
                                "/api/students/delete/{id}","/api/students/delete/{id}","/api/payments/status/{id}",
                                "/api/classes/update/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/classes/update/{id}","/api/students/{id}",
                                "/api/school-years/delete/{id}","/api/classes/delete/{id}").hasRole("ADMIN")
                        //obviously beside admin is student so authenticated is enough
                        //add more if needed
                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000")); // Restrict to localhost:3000
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public UserDetailsService userDetailService() {
        return userService::loadUserByUsername;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
