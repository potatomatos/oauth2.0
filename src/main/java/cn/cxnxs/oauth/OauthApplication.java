package cn.cxnxs.oauth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * <p>启动类</p>
 *
 * @author mengjinyuan
 * @date 2022-04-08 09:50
 **/
@SpringBootApplication
@MapperScan("cn.cxnxs.oauth.mapper")
@ServletComponentScan
@EnableTransactionManagement
@EnableDiscoveryClient
@EnableFeignClients
public class OauthApplication {
    public static void main(String[] args) {
        SpringApplication.run(OauthApplication.class, args);
    }
}
