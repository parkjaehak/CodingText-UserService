package org.userservice.userservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.userservice.userservice.jwt.CustomSuccessHandler;
import org.userservice.userservice.jwt.JwtFilter;
import org.userservice.userservice.service.OAuth2UserDetailsService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2UserDetailsService OAuth2UserDetailsService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(CsrfConfigurer::disable);
        http
                .formLogin(FormLoginConfigurer::disable);
        http
                .httpBasic(HttpBasicConfigurer::disable);
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/","/health","/auth/convert", "/favicon.ico", "/webjars/**","/error").permitAll()
                        .requestMatchers("/auth/signup").hasAnyAuthority("ROLE_USER_A") //최초 로그인일 경우 ROLE_USER_A가 할당되고 이때만 signup 접근가능
                        .anyRequest().authenticated());
        //JWTFilter
        http
                .addFilterAfter(jwtFilter, OAuth2LoginAuthenticationFilter.class);

        //oauth2 관련 서비스
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(OAuth2UserDetailsService))
                        .successHandler(customSuccessHandler));

        return http.build();
    }
}