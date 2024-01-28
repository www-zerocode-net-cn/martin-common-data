package com.java2e.martin.common.data;

import com.java2e.martin.common.data.mybatis.config.JsonInterceptor;
import com.java2e.martin.common.data.dynamic.spring.SpringSqlHelperDsManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author 狮少
 * @version 1.0
 * @date 2020/9/17
 * @describtion MartinDataAutoConfiguration
 * @since 1.0
 */
@Slf4j
@Configuration
@EnableAsync
@AllArgsConstructor
@ConditionalOnWebApplication
@ConditionalOnProperty(
        prefix = "martin.data",
        name = {"enabled"},
        havingValue = "true",
        matchIfMissing = true
)
@ComponentScan(basePackages = {"com.java2e.martin.common.data", "com.java2e.martin.common.core"})
public class MartinDataAutoConfiguration {
    @Bean
    public SpringSqlHelperDsManager springSqlHelperDsManager(){
        return new SpringSqlHelperDsManager();
    }

    @Bean
    public JsonInterceptor jsonInterceptor(){
        return new JsonInterceptor();
    }
}
