package com.firsthotel.member.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允許所有路徑
                .allowedOrigins("http://localhost:5173") // 替換為您的前端 URL
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 允許的 HTTP 方法
                .allowedHeaders("*")// 允許的標頭
                .allowCredentials(true);         		
    }
}
