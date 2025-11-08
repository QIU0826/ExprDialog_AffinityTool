package com.exprdialog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 排除默认的错误处理配置，避免NoResourceFoundException异常
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@EnableCaching
public class ExprDialogApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExprDialogApplication.class, args);
    }
    
    /**
     * 配置CORS，允许前端访问
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // 由于server.servlet.context-path已设置为"/api"，这里使用"/**"匹配所有路径
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8081")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
    
    /**
     * 简单的错误处理配置，防止NoResourceFoundException异常
     */
    @Bean
    public WebMvcConfigurer errorHandlingConfigurer() {
        return new WebMvcConfigurer() {
            // 不需要额外的错误处理配置，因为我们已经排除了默认的错误处理
            // 这样当找不到资源时，不会抛出异常而是返回404状态码
        };
    }
}