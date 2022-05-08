package cn.cxnxs.oauth.service;

import cn.cxnxs.oauth.config.security.entity.JwtUser;
import cn.cxnxs.oauth.config.security.entity.UserPasswordAuthenticationToken;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2022-05-04 15:35
 **/
public interface SystemService {

    /**
     * 获取验证码
     * @return
     */
    ByteArrayOutputStream getCaptcha();

    /**
     * 系统用户名密码登录
     * @param userPasswordAuthenticationToken
     * @return
     */
    JwtUser login(UserPasswordAuthenticationToken userPasswordAuthenticationToken);

    /**
     * 判断是否是允许通过的资源
     * @param uri
     * @return
     */
    boolean permit(String uri);

    /**
     * 获取白名单地址（不拦截的）
     * @return
     */
    List<String> permitPermissions();
}
