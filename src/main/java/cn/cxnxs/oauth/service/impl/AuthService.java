package cn.cxnxs.oauth.service.impl;


import cn.cxnxs.oauth.config.security.entity.AuthToken;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2022-04-11 16:39
 **/
@Service
@Slf4j
public class AuthService {

    final LoadBalancerClient loadBalancerClient;

    final RestTemplate restTemplate;

    final StringRedisTemplate redisTemplate;

    public AuthService(LoadBalancerClient loadBalancerClient, RestTemplate restTemplate, StringRedisTemplate redisTemplate) {
        this.loadBalancerClient = loadBalancerClient;
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 用户认证申请令牌 将令牌存储到redis
     *
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        AuthToken authToken = this.applyToken(username, password, clientId, clientSecret);
        if (authToken == null) {
            throw new RuntimeException();
        }
        //用户身份的令牌
        String accessToken = authToken.getAccessToken();
        //存储到redis
        //存储到redis中的内容
        String content = JSON.toJSONString(authToken);

        boolean token = this.saveToken(accessToken, content);
        if (!token) {
            throw new RuntimeException();
        }
        return authToken;
    }

    /**
     * 存储到redis
     *
     * @param accessToken 用户身份令牌
     * @param content      内容就是authtoken对象的内容
     * @return
     */
    private boolean saveToken(String accessToken, String content) {
        String key = "user_token:" + accessToken;
        redisTemplate.boundValueOps(key).set(content, 7200, TimeUnit.MINUTES);
        Long expire = redisTemplate.getExpire(key, TimeUnit.MINUTES);

        if (expire == null) {
            expire = 0L;
        }

        return expire > 0;
    }


    /**
     * 申请令牌
     *
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        //请求spring Security令牌
//        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
//        URI uri = serviceInstance.getUri();
        String authUrl = "http://localhost:9005/oauth/token";
        /**
         * url  就是申请令牌的url
         * method http的方法类型
         * requestEntity 请求内容
         * responseType 将响应的结果生成的类型
         */
        //请求的内容分为两部分

        //1 . header 信息 , 包括了http basic认证信息
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        String httpbasic = httpbasic(clientId, clientSecret);
        headers.add("Authorization", httpbasic);
        //2. 包括 : grant_type username password
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", password);
        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = new HttpEntity<>(body, headers);
        //指定restTemplate当遇到400或401响应的时候也不要抛出异常,也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {

                //当响应的值为400或401的时候也要正常响应,不要抛出异常
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });

        //远程调用申请令牌
        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, multiValueMapHttpEntity, Map.class);
        Map body1 = exchange.getBody();
        if (body1 == null ||
                body1.get("access_token") == null ||
                body1.get("refresh_token") == null ||
                body1.get("jti") == null) {
            //解析spring security
            if (body1 != null && body1.get("error_description") != null) {
                String error_description = (String) body1.get("error_description");
                if (error_description.contains("UserDetailsService returned null")) {
                    throw new RuntimeException();
                } else if (error_description.contains("坏的凭证")) {
                    throw new RuntimeException();
                }
            }

            return null;
        }
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken((String) body1.get("jti"));
        authToken.setRefreshToken((String) body1.get("refresh_token"));
        authToken.setJwtToken((String) body1.get("access_token"));
        return authToken;
    }

    /**
     * 得到basic编码
     *
     * @param clientId
     * @param clientSecret
     * @return
     */
    private String httpbasic(String clientId, String clientSecret) {
        //将客户端id和客户端密码拼接,按"客户端id,客户段密码"
        String string = clientId + ":" + clientSecret;
        //进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());

        return "Basic " + new String(encode);
    }

    /**
     * 从redis查询令牌
     *
     * @param token
     * @return
     */
    public AuthToken getUserToken(String token) {
        String key = "user_token:" + token;
        //从redis取到令牌信息
        String value = redisTemplate.opsForValue().get(key);
        try {

            return JSON.parseObject(value, AuthToken.class);
        } catch (Exception e) {
            log.error("getUserToken from redis and execute JSON parseObject error {}", e.getMessage());
            return null;
        }


    }

    /**
     * 从redis中删除令牌
     *
     * @param uid
     */
    public void delToken(String uid) {
        String key = "user_token:" + uid;
        redisTemplate.delete(key);
    }
}

