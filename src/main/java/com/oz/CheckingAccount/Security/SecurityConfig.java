package com.oz.CheckingAccount.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults())
                .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(HttpMethod.POST, "/accounts/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/accounts/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/accounts/**").hasRole("USER")
                    .anyRequest().denyAll());

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.withUsername("mary")
                                .password(passwordEncoder.encode("123987"))
                                .roles("USER").build();

        UserDetails oz = User.withUsername("oz")
                .password(passwordEncoder.encode("abc123"))
                .roles("USER").build();

        UserDetails tim = User.withUsername("tim")
                .password(passwordEncoder.encode("123456"))
                .roles("USER").build();
        return new InMemoryUserDetailsManager(user, oz, tim);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
