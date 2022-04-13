package cn.cxnxs.oauth.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2022-04-11 14:07
 **/
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String signingKey;

}
