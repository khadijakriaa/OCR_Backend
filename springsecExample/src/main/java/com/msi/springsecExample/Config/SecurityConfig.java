package com.msi.springsecExample.Config;

import com.msi.springsecExample.auth.CustomUserDetailsService;
import com.msi.springsecExample.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register","/auth/login", "/auth/logout", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UserDetailsService inMemoryUserDetailsService() {
        UserDetails user1 = User.builder()
                .username("admin")
                .password("password")
                .roles("USER")
                .build();
        UserDetails user2 = User.builder()
                .username("khadija")
                .password("h@123")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user1, user2);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use BCrypt for JWT authentication
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService inMemoryUserDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(inMemoryUserDetailsService);
        return provider;
    }
}
