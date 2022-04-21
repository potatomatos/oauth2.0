package cn.cxnxs.oauth.config.security;

/**
 * <p>认证失败处理器</p>
 *
 * @author mengjinyuan
 * @date 2022-04-14 00:13
 **/

import cn.cxnxs.common.vo.response.Result;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("myAuthenticationFailureHandler")
public class FailureHandler extends JSONAuthentication implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        Result<String> result;
        if (e instanceof AccountExpiredException) {
            //账号过期
            result = Result.failure(Result.ResultEnum.USER_ACCOUNT_EXPIRED,e.getMessage());
        } else if (e instanceof BadCredentialsException) {
            //密码错误
            result = Result.failure(Result.ResultEnum.USER_CREDENTIALS_ERROR,e.getMessage());
        } else if (e instanceof CredentialsExpiredException) {
            //密码过期
            result = Result.failure(Result.ResultEnum.USER_CREDENTIALS_EXPIRED,e.getMessage());
        } else if (e instanceof DisabledException) {
            //账号不可用
            result = Result.failure(Result.ResultEnum.USER_ACCOUNT_DISABLE,e.getMessage());
        } else if (e instanceof LockedException) {
            //账号锁定
            result = Result.failure(Result.ResultEnum.USER_ACCOUNT_LOCKED,e.getMessage());
        } else if (e instanceof InternalAuthenticationServiceException) {
            //用户不存在
            result = Result.failure(Result.ResultEnum.USER_ACCOUNT_NOT_EXIST,e.getMessage());
        }else{
            //其他错误
            result = Result.failure(Result.ResultEnum.COMMON_FAIL,e.getMessage());
        }
        this.writeJSON(httpServletRequest,httpServletResponse,result);
    }
}
