package cn.cxnxs.oauth.config.security.provider;

import cn.cxnxs.oauth.config.security.entity.JwtUser;
import cn.cxnxs.oauth.config.security.entity.UserPasswordAuthenticationToken;
import cn.cxnxs.oauth.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2022-05-06 22:44
 **/
public class UserPasswordAuthorizationProvider implements AuthenticationProvider {

    @Autowired
    private SystemService systemService;

    /**
     * 在此方法进行认证
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //认证代码，认证通过返回认证对象，失败返回null
        UserPasswordAuthenticationToken userPasswordAuthenticationToken = (UserPasswordAuthenticationToken)authentication;
        if(userPasswordAuthenticationToken.getUsername()==null || userPasswordAuthenticationToken.getPassword()==null){
            return null;
        }
        JwtUser user=systemService.login(userPasswordAuthenticationToken);
        if(user!=null){
            //返回认证后的Token
            userPasswordAuthenticationToken.setJwtUser(user);
            userPasswordAuthenticationToken.setAuthenticated(true);
            return userPasswordAuthenticationToken;
        }
        return null;
    }

    /**
     * 此方法决定Provider能够处理哪些Token，此Provider只能处理密码登录方式的Token，这里也是多种登录方式的核心
     * @param aClass
     * @return
     */
    @Override
    public boolean supports(Class<?> aClass) {
        //Manager传递token给provider，调用本方法判断该provider是否支持该token。不支持则尝试下一个filter
        //本类支持的token类：UserPasswordAuthenticationToken
        return (UserPasswordAuthenticationToken.class.isAssignableFrom(aClass));
    }
}
