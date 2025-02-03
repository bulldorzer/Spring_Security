package com.yourpackage.config;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@Log4j2
@RequiredArgsConstructor
public class CustomSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        log.info("----------------------------<Security Config>---------------------------------------");

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource (){

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // 모든 도메인 허용
        configuration.setAllowedMethods(Arrays.asList("HEAD","GET","POST","PUT","DELETE")); // 허용할 메서드
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control","Content-Type")); // 허용할 헤더 내용
        configuration.setAllowCredentials(true); // 자격 증명오류

        // url 기준으로 설정을 적용 할 것임 - 객체 생성하여 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
