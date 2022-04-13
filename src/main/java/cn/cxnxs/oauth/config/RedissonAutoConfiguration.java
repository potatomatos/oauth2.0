package cn.cxnxs.oauth.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * redisson配置
 * @author potatomato
 */
@Configuration
@EnableConfigurationProperties(value = RedissonProperties.class)
@ConditionalOnProperty(prefix = "redisson", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RedissonAutoConfiguration {

    public static final String MODE_SINGLE = "single";
    public static final String MODE_CLUSTER = "cluster";

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedissonClient redissonClient(RedissonProperties redisProperties) throws IOException {
        Config config = new Config();
        List<String> nodes = redisProperties.getNodes();
        if (Objects.equals(MODE_SINGLE, redisProperties.getMode())) {
            config.useSingleServer().setAddress("redis://"+nodes.get(0))
                    .setPassword(redisProperties.getPassword());
        } else {
            config.useClusterServers().addNodeAddress(nodes.stream().map(s -> "redis://" + s).collect(
                    Collectors.toList()).toArray(new String[nodes.size()]));
            if (StringUtils.isNotBlank(redisProperties.getPassword())) {
                config.useClusterServers().setPassword(redisProperties.getPassword());
            }
        }
        Codec codec = new JsonJacksonCodec();
        config.setCodec(codec);
        return Redisson.create(config);
    }
}
