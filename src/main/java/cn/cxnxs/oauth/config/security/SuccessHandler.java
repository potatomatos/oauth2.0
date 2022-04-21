package cn.cxnxs.oauth.config.security;

import cn.cxnxs.common.vo.response.Result;
import cn.cxnxs.oauth.config.security.entity.JwtUser;
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
        //只需要以json的格式返回一个提示就行了
        //Result类的Json数据
        JwtUser jwtUser= (JwtUser) authentication.getDetails();
        Result<JwtUser> result = Result.success("登录成功",jwtUser);
        this.writeJSON(httpServletRequest,httpServletResponse,result);
    }
}
