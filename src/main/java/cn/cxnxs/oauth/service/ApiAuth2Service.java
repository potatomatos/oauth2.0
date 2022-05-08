package cn.cxnxs.oauth.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * oauth2.0认证接口
 * @author mengjinyuan
 */
@FeignClient("auth-server")
public interface ApiAuth2Service {

    /**
     * 获取token
     * @param grant_type
     * @param client_id
     * @param client_secret
     * @param code
     * @param redirect_uri
     * @return
     */
    @GetMapping("/oauth/token")
    Map<String,String> getAccessToken(@RequestParam("grant_type")String grant_type,
                                      @RequestParam("client_id")String client_id,
                                      @RequestParam("client_secret")String client_secret,
                                      @RequestParam("code")String code,
                                      @RequestParam("redirect_uri")String redirect_uri);
}
