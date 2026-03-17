package org.example.reminderapp.config;

import org.example.reminderapp.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
//                Отключаю CSRF, она не нужна для rest api
                .csrf(csrf -> csrf.disable())

//                Настройка сессий через jwt
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
//                Тут делаю настройку авторизации, где выдаем доступ к эндпоинтам
                .authorizeHttpRequests(auth -> auth
//                       Доступ к публичным эндпоинтам разрешен для всех
                        .requestMatchers("/api/v1/user/create").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()

//                        Непубличные эндпоинты доступны только при авторизации
                                .anyRequest().authenticated()

                )
                .userDetailsService(userDetailsService);

        return http.build();
    }

}
