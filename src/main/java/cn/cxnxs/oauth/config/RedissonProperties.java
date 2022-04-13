package cn.cxnxs.oauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author redisson配置
 */
@Data
@ConfigurationProperties(prefix = "redisson")
public class RedissonProperties {
  private String mode = "single";
  private List<String> nodes;
  private String password;
  private boolean enabled;
}
