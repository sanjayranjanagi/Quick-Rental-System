package com.sanjay.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/auth/register", "/auth/login").permitAll()

                // CUSTOMER only API
                .requestMatchers("/quick-rent/rent-vehicle",
                                 "/quick-rent/return-vehicle",
                                 "/quick-rent/rent-history/**",
                                 "/quick-rent/search",
                                 "/quick-rent/extend-booking",
                                 "/quick-rent/cancel-booking")
                .hasRole("CUSTOMER")

                // AGENT only APIs
                .requestMatchers("/quick-rent/add-vehicle",
                                 "/quick-rent/delete")
                .hasRole("AGENT")

                // Both AGENT + CUSTOMER can see available vehicles
                .requestMatchers("/quick-rent/available-vehicles")
                .authenticated()

                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
