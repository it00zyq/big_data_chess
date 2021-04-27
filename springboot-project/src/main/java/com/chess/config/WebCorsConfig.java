package com.chess.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author it00zyq
 * 跨域配置
 */
@Configuration
public class WebCorsConfig implements WebMvcConfigurer {

    /**
     * 跨域问题解决
     * 允许任何域名使用
     * 允许任何请求头头
     * 允许任何方法（post、get等）
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .allowedMethods("*");
    }

}
