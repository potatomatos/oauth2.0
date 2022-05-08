package cn.cxnxs.oauth.config.security;

import cn.cxnxs.common.vo.response.Result;
import cn.cxnxs.oauth.config.security.entity.JwtUser;
import cn.cxnxs.oauth.config.security.entity.UserPasswordAuthenticationToken;
import cn.cxnxs.oauth.vo.RedisKeyPrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * <p>登出处理器</p>
 *
 * @author mengjinyuan
 * @date 2022-04-14 09:59
 **/
@Component("myLogoutHandler")
public class LogoutHandler extends JSONAuthentication implements LogoutSuccessHandler {

    @Autowired
    private TokenStore jdbcTokenStore;

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        //删除token信息
        String token = httpServletRequest.getHeader("token");
        UserPasswordAuthenticationToken authenticationToken= (UserPasswordAuthenticationToken) authentication;
        JwtUser jwtUser = authenticationToken.getJwtUser();
        OAuth2AccessToken oAuth2AccessToken = jdbcTokenStore.readAccessToken(token);
        if (!Objects.isNull(oAuth2AccessToken)){
            jdbcTokenStore.removeAccessToken(oAuth2AccessToken);
        }
        //删除缓存信息
        redisTemplate.delete(RedisKeyPrefix.KEY_USER_INFO+jwtUser.getId());
        Result<String> result = Result.success("注销成功");
        this.writeJSON(httpServletRequest,httpServletResponse,result);
    }
}
