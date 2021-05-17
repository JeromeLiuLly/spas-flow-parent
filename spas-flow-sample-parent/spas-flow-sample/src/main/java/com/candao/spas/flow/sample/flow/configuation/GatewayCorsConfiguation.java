package com.candao.spas.flow.sample.flow.configuation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GatewayCorsConfiguation {

    @Bean
    public CorsFilter corsFilter(){
        // 初始化cors配置对象
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true); // 允许使用cookie，但是使用cookie是addAllowedOrigin必须是具体的地址，不能是*
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");  //允许的请求方式,get,put,post,delete
        configuration.addAllowedHeader("*");//允许的头信息

        //初始化cors的源对象配置
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**",configuration);

        //3.返回新的CorsFilter.
        return new CorsFilter(corsConfigurationSource);
    }
}