package cn.cxnxs.oauth.config.security;

import cn.cxnxs.oauth.config.security.entity.JwtUser;
import cn.cxnxs.oauth.entity.SysUsers;
import cn.cxnxs.oauth.mapper.SysUsersMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>用户信息配置</p>
 *
 * @author mengjinyuan
 * @date 2022-04-09 23:14
 **/
public class UserDetailServiceImpl implements UserDetailsService {

    @Resource
    private SysUsersMapper sysUsersMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        //从数据库获取权限
        SysUsers sysUsers = sysUsersMapper.selectOne(new LambdaQueryWrapper<SysUsers>().eq(SysUsers::getUsername, username));
        if (sysUsers==null){
            //表示用户不存在
            throw  new UsernameNotFoundException("用户名或密码错误");
        }
        //取出正确密码（密文）
        String password = sysUsers.getEncryptedPassword();
        boolean enabled=true;
        boolean accountNonExpired=true;
        boolean credentialsNonExpired=true;
        boolean accountNonLocked=true;

        if (JwtUser.USER_STATE.DISABLED.getCode().equals(sysUsers.getState())){
            enabled=false;
        }
        if (JwtUser.USER_STATE.ACCOUNT_EXPIRED.getCode().equals(sysUsers.getState())){
            accountNonExpired=false;
        }
        if (JwtUser.USER_STATE.CREDENTIALS_EXPIRED.getCode().equals(sysUsers.getState())){
            credentialsNonExpired=false;
        }
        if (JwtUser.USER_STATE.ACCOUNT_LOCKED.getCode().equals(sysUsers.getState())){
            accountNonLocked=false;
        }
        //获取用户权限
        Integer userId=sysUsers.getId();
        //获取用户客户端信息
        List<String> userClients = sysUsersMapper.getUserClients(userId);
        //获取用户的所有角色
        List<String> userRoles = sysUsersMapper.getUserRoles(userId);
        //获取用户的权限信息
        List<Map<String, String>> permissions = sysUsersMapper.getUserPermissions(userId);
        //根据角色分组权限
        Map<String,List<Map<String, String>>> rolePermissions=permissions.stream().collect(Collectors.groupingBy(e->e.get("role_code")));


        JwtUser userDetails = new JwtUser(username,
                password,
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",", userRoles)));
        userDetails.setId(userId);
        userDetails.setUsername(sysUsers.getUsername());
        userDetails.setAvatar(sysUsers.getAvatar());
        userDetails.setPhoneNumber(sysUsers.getPhoneNumber());
        userDetails.setEmail(sysUsers.getEmail());
        userDetails.setPermissions(permissions);
        userDetails.setUserClients(userClients);
        userDetails.setUserRoles(userRoles);
        userDetails.setRolePermissions(rolePermissions);
        return userDetails;
    }
}
