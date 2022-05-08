package cn.cxnxs.oauth.config.security.entity;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2022-05-06 21:16
 **/
public class UserPasswordAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * oauth认证获取授权码的一些信息
     */
    private String clientId;

    private String redirectUri;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 验证码
     */
    private String captcha;

    /**
     * 登录类型
     */
    private String type;

    /**
     * 用户信息
     */
    private JwtUser jwtUser;

    public UserPasswordAuthenticationToken() {
        super(null);
        super.setAuthenticated(false);
    }

    /**
     * 前端传参的时候调用
     *
     * @param clientId
     * @param redirectUri
     * @param username
     * @param password
     * @param captcha
     */
    public UserPasswordAuthenticationToken(String clientId, String redirectUri, String username, String password, String captcha) {
        super(null);
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.username = username;
        this.password = password;
        this.captcha = captcha;
        super.setAuthenticated(false);
    }

    /**
     * 认证通过后Provider通过这个方法创建Token，传入自定义信息以及授权信息
     */
    public UserPasswordAuthenticationToken(JwtUser jwtUser) {
        super(null);
        this.jwtUser = jwtUser;
        //关键：标记已认证
        super.setAuthenticated(true);
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public JwtUser getJwtUser() {
        return jwtUser;
    }

    public void setJwtUser(JwtUser jwtUser) {
        this.jwtUser = jwtUser;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * 父类获取授权信息的两个方法，区别是啥不太清楚，但都可以返回自定义信息
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return jwtUser;
    }

}
