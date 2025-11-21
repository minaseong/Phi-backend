package com.backend.phi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 모든 origin 허용 (개발 환경용)
        // 프로덕션에서는 특정 도메인만 허용하도록 변경해야 합니다
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // 모든 origin 허용
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("*"); // 모든 HTTP 메서드 허용 (GET, POST, PUT, DELETE 등)
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

