package cn.cxnxs.oauth.config.security;

import cn.cxnxs.common.vo.response.Result;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>登出处理器</p>
 *
 * @author mengjinyuan
 * @date 2022-04-14 09:59
 **/
@Component("myLogoutHandler")
public class LogoutHandler extends JSONAuthentication implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        Result<String> result = Result.success("注销成功");
        this.writeJSON(httpServletRequest,httpServletResponse,result);
    }
}
