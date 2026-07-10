package com.zhoukai.librarysystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
/** 配置浏览器跨域访问规则。 */
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 开发环境允许不同地址的前端访问后端。
        config.addAllowedOriginPattern("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        // 允许浏览器携带 Session 对应的 Cookie，否则登录状态无法保持。
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 将上面的规则应用到后端全部路径。
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
