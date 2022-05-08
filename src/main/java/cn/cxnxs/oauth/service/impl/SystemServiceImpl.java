package cn.cxnxs.oauth.service.impl;

import cn.cxnxs.common.utils.ImageUtil;
import cn.cxnxs.common.utils.StringUtil;
import cn.cxnxs.oauth.config.security.UserDetailServiceImpl;
import cn.cxnxs.oauth.config.security.entity.JwtUser;
import cn.cxnxs.oauth.config.security.entity.UserPasswordAuthenticationToken;
import cn.cxnxs.oauth.entity.SysPermission;
import cn.cxnxs.oauth.mapper.SysPermissionMapper;
import cn.cxnxs.oauth.service.SystemService;
import cn.cxnxs.oauth.utils.RedisUtils;
import cn.cxnxs.oauth.vo.RedisKeyPrefix;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2022-05-04 15:36
 **/
@Slf4j
@Service
public class SystemServiceImpl implements SystemService {


    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Resource
    private SysPermissionMapper sysPermissionMapper;


    /**
     * 获取验证码
     *
     * @return
     */
    @Override
    public ByteArrayOutputStream getCaptcha() {
        String captcha = StringUtil.randomString(4);
        String key = RedisKeyPrefix.KEY_CAPTCHA + this.getIpAddr();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageUtil.buildImageVerify(captcha, 100, 38, bos);
        redisUtils.set(key, captcha, 5 * 60);
        return bos;
    }

    /**
     * 系统用户名密码登录
     *
     * @param userPasswordAuthenticationToken 登录信息
     * @return
     */
    @Override
    public JwtUser login(UserPasswordAuthenticationToken userPasswordAuthenticationToken) {
        //判空
        if (StringUtil.isEmpty(userPasswordAuthenticationToken.getUsername())) {
            throw new AuthenticationServiceException("用户名为空");
        }
        if (StringUtil.isEmpty(userPasswordAuthenticationToken.getPassword())) {
            throw new AuthenticationServiceException("密码为空");
        }
        if (StringUtil.isEmpty(userPasswordAuthenticationToken.getCaptcha())) {
            throw new AuthenticationServiceException("验证码为空");
        }
        if (StringUtil.isEmpty(userPasswordAuthenticationToken.getClientId())) {
            throw new AuthenticationServiceException("clientId为空");
        }
        if (StringUtil.isEmpty(userPasswordAuthenticationToken.getRedirectUri())) {
            throw new AuthenticationServiceException("回调地址为空");
        }
        //获取验证码
        String key = RedisKeyPrefix.KEY_CAPTCHA + this.getIpAddr();
        String captcha = redisUtils.get(key);
        if (!userPasswordAuthenticationToken.getCaptcha().equalsIgnoreCase(captcha)) {
            throw new AuthenticationServiceException("验证码错误");
        }
        //查询用户信息
        UserDetails userDetails = userDetailService.loadUserByUsername(userPasswordAuthenticationToken.getUsername());
        //判断密码
        if (!passwordEncoder.matches(userPasswordAuthenticationToken.getPassword(), userDetails.getPassword())){
            throw new BadCredentialsException("密码错误");
        }
        //将用户信息保存到redis
        redisTemplate.opsForValue().set(RedisKeyPrefix.KEY_USER_INFO+((JwtUser) userDetails).getId(), JSON.toJSONString(userDetails));
        //删除验证码
        redisTemplate.delete(key);
        return (JwtUser) userDetails;
    }

    /**
     * 判断是否是允许通过的资源
     *
     * @param uri
     * @return
     */
    @Override
    public boolean permit(String uri) {
        List<String> permitPermissions = this.permitPermissions();
        AntPathMatcher antPathMatcher=new AntPathMatcher();
        for (String permitPermission : permitPermissions) {
            if (antPathMatcher.match(permitPermission,uri)){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取白名单地址（不拦截的）
     *
     * @return
     */
    @Override
    public List<String> permitPermissions() {
        return sysPermissionMapper.selectList(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getPermissionType,1))
                .stream().map(SysPermission::getApi).collect(Collectors.toList());
    }

    /**
     * 获取客户端ip
     *
     * @return
     */
    private String getIpAddr() {
        String ipAddress;
        try {
            ipAddress = httpServletRequest.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = httpServletRequest.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = httpServletRequest.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = httpServletRequest.getRemoteAddr();
                String localhost = "127.0.0.1";
                if (localhost.equals(ipAddress)) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet;
                    try {
                        inet = InetAddress.getLocalHost();
                        ipAddress = inet.getHostAddress();
                    } catch (UnknownHostException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ipAddress = "";
        }
        return ipAddress;
    }
}
