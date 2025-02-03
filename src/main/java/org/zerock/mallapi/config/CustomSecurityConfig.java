package org.zerock.mallapi.config;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.zerock.mallapi.security.handler.APILoginFailHandler;
import org.zerock.mallapi.security.handler.APILoginSuccessHandler;

@Configuration
@Log4j2
@RequiredArgsConstructor
public class CustomSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        log.info("----------------------------<Security Config>---------------------------------------");

        // p302
        // 1. http.cors : cors 정책 적용
        http.cors(httpSecurityCorsConfigurer -> {
           httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
        });

        // 2. 세션 사용하지 않음
        http.sessionManagement(sessionConfig ->
                sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 3. CSRF 비활성화
        http.csrf(config-> config.disable());

        // p315
        // 4. 로그인 설정
        http.formLogin(config -> {
            config.loginPage("api/member/login"); // 로그인 page url
            config.successHandler(new APILoginSuccessHandler());
            config.failureHandler(new APILoginFailHandler());


        });

        return http.build();
    }

    //p302
    // Spring Boot에서 CORS 정책을 글로벌하게 적용하는 설정
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

    // p303
    // 비밀번호 인코딩
    @Bean
    public PasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();
    }
}
