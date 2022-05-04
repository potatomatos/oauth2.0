package cn.cxnxs.oauth.config.security.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>用户信息</p>
 *
 * @author mengjinyuan
 * @date 2022-04-10 23:11
 **/
public class JwtUser extends User {

    private Integer id;
    private String username;
    private List<String> userClients;
    private List<String> userRoles;
    private List<Map<String, String>> permissions;
    private Map<String,List<Map<String, String>>> rolePermissions;

    public JwtUser(String username,
                   String password,
                   boolean enabled,
                   boolean accountNonExpired,
                   boolean credentialsNonExpired,
                   boolean accountNonLocked,
                   Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
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

    public List<String> getUserClients() {
        return userClients;
    }

    public void setUserClients(List<String> userClients) {
        this.userClients = userClients;
    }

    public List<String> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<String> userRoles) {
        this.userRoles = userRoles;
    }

    public List<Map<String, String>> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Map<String, String>> permissions) {
        this.permissions = permissions;
    }

    public Map<String, List<Map<String, String>>> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(Map<String, List<Map<String, String>>> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    /**
     * 用户状态
     */
    public enum USER_STATE{
        ENABLED(0,"启用"),
        DISABLED(1,"禁用"),
        ACCOUNT_EXPIRED(2,"账号过期"),
        CREDENTIALS_EXPIRED(3,"密码过期"),
        ACCOUNT_LOCKED(4,"账号锁定")
        ;
        /**
         * 码值
         */
        final private Integer code;

        /**
         * 描述
         */
        final private String desc;

        USER_STATE(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}
