package cn.cxnxs.oauth.config.security;

import cn.cxnxs.common.vo.response.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2022-05-05 02:11
 **/
@Component("myAccessDeniedHandler")
public class DeniedHandler extends JSONAuthentication implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        this.writeJSON(request,response, Result.failure(accessDeniedException.getMessage()));
    }
}
