package com.leyou.config;

import com.leyou.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class MvcConfig  implements WebMvcConfigurer {
    @Autowired
    private  JwtProperties jwtProperties;

    @Bean
    public LoginInterceptor loginInterceptor(){
        LoginInterceptor loginInterceptor = new LoginInterceptor(jwtProperties);
        return  loginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.loginInterceptor()).addPathPatterns("/**");
    }
}
