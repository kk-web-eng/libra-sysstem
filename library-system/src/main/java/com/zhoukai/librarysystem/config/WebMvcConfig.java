package com.zhoukai.librarysystem.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
/** 把登录拦截器注册到 Spring MVC。 */
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                // 默认保护所有 /api 接口。
                .addPathPatterns("/api/**")
                // 登录、公共馆藏和读者端接口使用各自的登录逻辑，因此排除。
                .excludePathPatterns("/api/login", "/api/public/**", "/api/user/**");
    }
}
