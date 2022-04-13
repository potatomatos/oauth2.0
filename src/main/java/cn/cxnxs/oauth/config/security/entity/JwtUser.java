package cn.cxnxs.oauth.config.security.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * <p>用户信息</p>
 *
 * @author mengjinyuan
 * @date 2022-04-10 23:11
 **/
public class JwtUser extends User {

    private Integer id;
    private String username;

    public JwtUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.username=username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
