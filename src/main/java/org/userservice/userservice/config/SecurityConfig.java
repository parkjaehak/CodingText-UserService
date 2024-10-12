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
import org.userservice.userservice.error.AccessDeniedHandlerCustom;
import org.userservice.userservice.error.AuthenticationEntryPointCustom;
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
    private final AuthenticationEntryPointCustom authenticationEntryPointCustom;
    private final AccessDeniedHandlerCustom accessDeniedHandlerCustom;

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
                        .requestMatchers("/", "/health", "/favicon.ico", "/webjars/**", "/error").permitAll()
                        .requestMatchers("/auth/signup","/auth/cookie-to-header").permitAll()
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
        http.
                exceptionHandling(authenticationManager-> authenticationManager
                        .authenticationEntryPoint(authenticationEntryPointCustom) // 401 Error 처리, 인증과정에서 실패할 시 처리
                        .accessDeniedHandler(accessDeniedHandlerCustom)); // 403 Error 처리, role, authority 권한 관련 에러

        return http.build();
    }
}