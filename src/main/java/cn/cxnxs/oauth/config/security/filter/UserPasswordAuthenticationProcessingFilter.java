package cn.cxnxs.oauth.config.security.filter;

import cn.cxnxs.oauth.config.security.entity.UserPasswordAuthenticationToken;
import cn.cxnxs.oauth.enumerate.LoginType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2022-05-06 21:09
 **/
public class UserPasswordAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    ObjectMapper objectMapper;

    public UserPasswordAuthenticationProcessingFilter() {
        //认证url
        super("/login");
    }


    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException {
        UserPasswordAuthenticationToken userPasswordAuthenticationToken = new UserPasswordAuthenticationToken();
        BeanUtils.populate(userPasswordAuthenticationToken, request.getParameterMap());

        //根据前端参数判断是哪种登录类型，封装成对应方式的Token，提交给Manager
        if (LoginType.PASSWORD.toString().equals(userPasswordAuthenticationToken.getType())) {
            //把账号密码封装成token，传给manager认证
            return getAuthenticationManager().authenticate(userPasswordAuthenticationToken);
        } else {
            //其他登录方式
            throw new AuthenticationServiceException("登录类型有误");
        }
    }
}
