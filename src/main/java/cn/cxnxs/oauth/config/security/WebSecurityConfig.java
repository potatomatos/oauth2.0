package cn.cxnxs.oauth.config.security;

import cn.cxnxs.oauth.config.security.filter.JwtAuthenticationTokenFilter;
import cn.cxnxs.oauth.config.security.filter.UserPasswordAuthenticationProcessingFilter;
import cn.cxnxs.oauth.config.security.provider.UserPasswordAuthorizationProvider;
import cn.cxnxs.oauth.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * <p>springSecurity配置</p>
 *
 * @author mengjinyuan
 * @date 2022-04-10 20:20
 **/
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SuccessHandler successHandler;

    @Autowired
    private FailureHandler failureHandler;

    @Autowired
    private LogoutHandler logoutHandler;

    @Autowired
    private DeniedHandler deniedHandler;

    @Autowired
    private SystemService systemService;

    /**
     * 核心：filter配置
     */
    public UserPasswordAuthenticationProcessingFilter userPasswordAuthenticationProcessingFilter(AuthenticationManager authenticationManager) {
        UserPasswordAuthenticationProcessingFilter userPasswordAuthenticationProcessingFilter = new UserPasswordAuthenticationProcessingFilter();
        //为filter设置管理器
        userPasswordAuthenticationProcessingFilter.setAuthenticationManager(authenticationManager);
        userPasswordAuthenticationProcessingFilter.setAuthenticationSuccessHandler(successHandler);
        userPasswordAuthenticationProcessingFilter.setAuthenticationFailureHandler(failureHandler);
        return userPasswordAuthenticationProcessingFilter;
    }

    /**
     * 核心：provider配置
     */
    @Bean
    public UserPasswordAuthorizationProvider userPasswordAuthorizationProvider() {
        return new UserPasswordAuthorizationProvider();
    }

    /**
     *token校验过滤器
     */
    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter(){
        return new JwtAuthenticationTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * 用户信息
     */
    @Bean
    public UserDetailsService userService() {
        return new UserDetailServiceImpl();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 获取用户信息
        auth.userDetailsService(userService());
        auth.authenticationProvider(userPasswordAuthorizationProvider());
    }

    @Override
    public void configure(WebSecurity web) {
        //解决静态资源被拦截的问题
        web.ignoring().antMatchers("/static/**");
    }

    /**
     * 配置访问策略
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //允许在iframe中加载页面
        http.headers().frameOptions().disable();
        http.csrf().disable()
                //不通过Session获取SecurityContext
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
                .authorizeRequests()
                .antMatchers(systemService.permitPermissions().toArray(new String[0])).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin().loginPage("/login.html").loginProcessingUrl("/login").permitAll()
                //此处写登录成功后的操作无效，userPasswordAuthenticationProcessingFilter（）已接管此类设置
//                .successHandler(successHandler).permitAll()
//                .failureHandler(failureHandler).permitAll()
                .and()
                .logout().permitAll().logoutSuccessHandler(logoutHandler)
                .invalidateHttpSession(true).clearAuthentication(true)
                .and()
                //配置没有权限的自定义处理类
                .exceptionHandling().accessDeniedHandler(deniedHandler)
                .and()
                .httpBasic();
        //token验证过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        //核心：添加过滤器，注意先后顺序
        http.addFilterBefore(userPasswordAuthenticationProcessingFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);

    }

    /**
     * oauth密码模式需要拿到这个bean
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
