package cn.cxnxs.oauth.config.security;

import cn.cxnxs.oauth.config.security.entity.JwtUser;
import cn.cxnxs.oauth.entity.SysUsers;
import cn.cxnxs.oauth.mapper.SysUsersMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
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
        SysUsers oauthUsers = sysUsersMapper.selectOne(new LambdaQueryWrapper<SysUsers>().eq(SysUsers::getUsername, username));
        if (oauthUsers==null){
            //表示用户不存在
            return null;
        }
        //取出正确密码（hash值）
        String password = oauthUsers.getEncryptedPassword();
        //从数据库获取权限
        JwtUser userDetails = new JwtUser(username,
                password,
                AuthorityUtils.commaSeparatedStringToAuthorityList(""));
        userDetails.setId(oauthUsers.getId());
        userDetails.setUsername(oauthUsers.getUsername());
        return userDetails;
    }
}
