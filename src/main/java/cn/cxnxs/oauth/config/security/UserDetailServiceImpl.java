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
        SysUsers oauthUsers = sysUsersMapper.selectOne(new LambdaQueryWrapper<SysUsers>().eq(SysUsers::getUsername, username));
        if (oauthUsers==null){
            //表示用户不存在
            throw  new UsernameNotFoundException("用户名或密码错误");
        }
        //取出正确密码（密文）
        String password = oauthUsers.getEncryptedPassword();
        boolean enabled=true;
        boolean accountNonExpired=true;
        boolean credentialsNonExpired=true;
        boolean accountNonLocked=true;

        if (JwtUser.USER_STATE.DISABLED.getCode().equals(oauthUsers.getState())){
            enabled=false;
        }
        if (JwtUser.USER_STATE.ACCOUNT_EXPIRED.getCode().equals(oauthUsers.getState())){
            accountNonExpired=false;
        }
        if (JwtUser.USER_STATE.CREDENTIALS_EXPIRED.getCode().equals(oauthUsers.getState())){
            credentialsNonExpired=false;
        }
        if (JwtUser.USER_STATE.ACCOUNT_LOCKED.getCode().equals(oauthUsers.getState())){
            accountNonLocked=false;
        }
        JwtUser userDetails = new JwtUser(username,
                password,
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                AuthorityUtils.commaSeparatedStringToAuthorityList(""));
        userDetails.setId(oauthUsers.getId());
        userDetails.setUsername(oauthUsers.getUsername());
        return userDetails;
    }
}
