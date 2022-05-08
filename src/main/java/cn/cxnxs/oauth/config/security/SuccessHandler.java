package cn.cxnxs.oauth.config.security;

import cn.cxnxs.common.vo.response.Result;
import cn.cxnxs.oauth.config.security.entity.JwtUser;
import cn.cxnxs.oauth.config.security.entity.UserPasswordAuthenticationToken;
import com.alibaba.fastjson.JSON;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>认证成功处理器</p>
 *
 * @author mengjinyuan
 * @date 2022-04-14 10:04
 **/
@Component("myAuthenticationSuccessHandler")
public class SuccessHandler extends JSONAuthentication implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        UserPasswordAuthenticationToken authenticationToken= (UserPasswordAuthenticationToken) authentication;
        String authorizeUrl="/oauth/authorize?response_type=code&client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}";
        authorizeUrl=authorizeUrl
                .replace("${CLIENT_ID}",authenticationToken.getClientId())
                .replace("${REDIRECT_URI}",authenticationToken.getRedirectUri());
        //Result类的Json数据
        if (httpServletRequest.getHeader("x-requested-with") != null
                && "XMLHttpRequest".equalsIgnoreCase(httpServletRequest.getHeader("x-requested-with"))) {
            //返回json
            Result<Object> result = Result.success("登陆成功",authorizeUrl);
            this.writeJSON(httpServletRequest,httpServletResponse,result);
        } else {
            //返回页面
            httpServletResponse.setStatus(302);
            httpServletResponse.sendRedirect(authorizeUrl);
        }

    }
}
