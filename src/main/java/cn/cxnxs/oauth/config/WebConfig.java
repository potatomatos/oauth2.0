package cn.cxnxs.oauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2022-04-11 20:54
 **/
@Configuration
public class WebConfig {


    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
