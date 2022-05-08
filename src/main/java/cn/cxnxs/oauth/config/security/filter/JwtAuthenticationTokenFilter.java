package cn.cxnxs.oauth.config.security.filter;

import cn.cxnxs.common.vo.response.Result;
import cn.cxnxs.oauth.config.security.UserDetailServiceImpl;
import cn.cxnxs.oauth.config.security.entity.JwtUser;
import cn.cxnxs.oauth.config.security.entity.UserPasswordAuthenticationToken;
import cn.cxnxs.oauth.service.SystemService;
import cn.cxnxs.oauth.utils.WebUtils;
import cn.cxnxs.oauth.vo.RedisKeyPrefix;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * <p></p>
 *
 * @author mengjinyuan
 * @date 2022-05-07 23:07
 **/
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private TokenStore jdbcTokenStore;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //已认证直接放行
        if (authentication!=null&&authentication.isAuthenticated()){
            filterChain.doFilter(request, response);
            return;
        }

        //不需要token的路由可以直接放行
        if (systemService.permit(uri)) {
            filterChain.doFilter(request, response);
            return;
        }
        //1、获取请求头携带的token
        String accessToken = request.getHeader("access_token");
        //header中没有就从参数中获取
        if (StringUtils.isEmpty(accessToken)) {
            accessToken = request.getParameter("access_token");
        }

        if (!StringUtils.hasText(accessToken)) {
            render(request, response, Result.failure(Result.ResultEnum.NEED_LOGIN, null));
            return;
        }

        //2、解析出userId
        JwtUser jwtUser;
        try {
            OAuth2Authentication oAuth2Authentication = jdbcTokenStore.readAuthentication(accessToken);
            if (Objects.isNull(oAuth2Authentication)) {
                render(request, response, Result.failure(Result.ResultEnum.TOKEN_REQUIRED, null));
                return;
            }
            OAuth2AccessToken oAuth2AccessToken = jdbcTokenStore.readAccessToken(accessToken);
            if (oAuth2AccessToken.isExpired()) {
                //token过期
                render(request, response, Result.failure(Result.ResultEnum.TOKEN_EXPIRED, null));
                return;
            }
            jwtUser = (JwtUser) oAuth2Authentication.getPrincipal();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //token超时或者非法
            render(request, response, Result.failure(Result.ResultEnum.NEED_LOGIN, e.getMessage()));
            return;
        }
        //3、从redis中获取用户信息
        JwtUser jwtUserCache = JSONObject.parseObject(redisTemplate.opsForValue().get(RedisKeyPrefix.KEY_USER_INFO + jwtUser.getId()), JwtUser.class);
        if (Objects.isNull(jwtUserCache)) {
            jwtUser = (JwtUser) userDetailService.loadUserByUsername(jwtUser.getUsername());
        } else {
            jwtUser = jwtUserCache;
        }
        //4、如果能返回用户信息，存入SecurityContextHolder
        UserPasswordAuthenticationToken authenticationToken = new UserPasswordAuthenticationToken(jwtUser);
        authenticationToken.setUsername(jwtUser.getUsername());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //放行
        filterChain.doFilter(request, response);
    }

    /**
     * 渲染视图
     *
     * @param request
     * @param response
     */
    private void render(HttpServletRequest request, HttpServletResponse response, Object data) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        if (request.getHeader("x-requested-with") != null
                && "XMLHttpRequest".equalsIgnoreCase(request.getHeader("x-requested-with"))) {
            WebUtils.renderJSON(response, data);
        }
//        else {
//            //返回页面
//            WebUtils.renderPage(response, "/666");
//        }
    }
}
