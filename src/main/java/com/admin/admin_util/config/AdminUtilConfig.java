package com.admin.admin_util.config;

import com.admin.admin_util.aop.AdminUtilAspect;
import com.admin.admin_util.properties.AdminUtilProperties;
import com.admin.admin_util.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * @author 陈群矜
 */
@Configuration
@EnableConfigurationProperties({AdminUtilProperties.class})
@ConditionalOnProperty(prefix = "admin.util", value = "enabled")
public class AdminUtilConfig {

    @Autowired
    private AdminUtilProperties adminUtilProperties;

    @Bean
    @ConditionalOnMissingBean
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil();
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        builder.setConnectTimeout(Duration.ofMillis(8 * 1000));
        builder.setReadTimeout(Duration.ofMillis(8 * 1000));
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public AdminUtilAspect adminUtilAspect() {
        return new AdminUtilAspect(adminUtilProperties.getIp(), adminUtilProperties.getPort());
    }

}
