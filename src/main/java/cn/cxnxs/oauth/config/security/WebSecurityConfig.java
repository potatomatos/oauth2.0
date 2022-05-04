package cn.cxnxs.oauth.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
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
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/static/**", "/sys/captcha", "/login", "/rsa/publicKey", "/oauth/**", "swagger-ui.html", "/webjars/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin().loginPage("/sys/login.html").loginProcessingUrl("/sys/login").permitAll()
                .successHandler(successHandler).permitAll()
                .failureHandler(failureHandler).permitAll()
                .and()
                .logout().logoutUrl("/logout").logoutSuccessHandler(logoutHandler)
                .invalidateHttpSession(true).clearAuthentication(true)
                .and()
                //配置没有权限的自定义处理类
                .exceptionHandling().accessDeniedHandler(deniedHandler)
                .and()
                .httpBasic();
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
