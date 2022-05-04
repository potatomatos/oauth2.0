package cn.cxnxs.oauth.config.security;

import cn.cxnxs.oauth.config.security.entity.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.HashMap;

/**
 * <p>授权服务器配置</p>
 *
 * @author mengjinyuan
 * @date 2022-04-10 20:27
 **/
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailsService userService;


    /**
     * 客户端信息
     */
    @Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(this.dataSource);
    }

    @Bean
    public TokenStore jdbcTokenStore() {
        Assert.state(this.dataSource != null, "DataSource must be provided");
        return new JdbcTokenStore(this.dataSource);
    }

    /**
     * 授权码存储方式，其实这个也可以不写，因为用的是密码模式，没有授权码
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource) {
        return new JdbcAuthorizationCodeServices(dataSource);
    }


    /**
     * 生成jwt令牌
     *
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter() {
            /***
             * 重写增强token方法,用于自定义一些token总需要封装的信息
             * @return
             */
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
                JwtUser jwtUser= (JwtUser) authentication.getPrincipal();
                HashMap<String,Object> additionalInformation = new HashMap<>();
                //把用户ID设置到JWT中
                additionalInformation.put("userId",jwtUser.getId());
                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
                return super.enhance(accessToken, authentication);
            }
        };
        // 生成签名的key,资源服务使用相同的字符达到一个对称加密的效果,生产时候使用RSA非对称加密方式
        accessTokenConverter.setKeyPair(keyPair());
        return accessTokenConverter;
    }

    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
        return factory.getKeyPair("jwt", "123456".toCharArray());
    }

    /**
     * 授权服务器端点配置
     * 密码模式需要outh2授权服务器去校验账号、密码
     * 账号、密码在整合security时，放在security中了
     * 所以需要整合security的授权管理器authenticationManagerBean
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.accessTokenConverter(this.jwtAccessTokenConverter())
                //使用密码模式需要配置
                .authenticationManager(this.authenticationManager)
                //配置授权码存储方式
                .authorizationCodeServices(this.authorizationCodeServices(this.dataSource))
                //指定token存储到redis，还有数据库、内存、jwt等存储方式！
                .tokenStore(jdbcTokenStore())
                //支持refresh_token机制
                .reuseRefreshTokens(false)
                //这一步包含账号、密码的检查！
                .userDetailsService(userService)
                //支持GET,POST请求
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }

    /**
     * 授权服务器的安全配置
     *
     * @param security
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        //允许表单认证
        security.allowFormAuthenticationForClients()
                //密码加密器
                .passwordEncoder(this.passwordEncoder)
                // 开启/oauth/token_key验证端口无权限访问
                .tokenKeyAccess("permitAll()")
                // 开启/oauth/check_token验证端口认证权限访问
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        /*
         *授权码模式
         *http://localhost:8080/oauth/authorize?response_type=code&client_id=client&redirect_uri=http://www.baidu.com&scope=all
         *http://localhost:8080/oauth/authorize?response_type=code&client_id=client
         *
         * implicit: 简化模式
         *http://localhost:8080/oauth/authorize?client_id=client&response_type=token&scope=all&redirect_uri=http://www.baidu.com
         *
         * password模式
         *  http://localhost:8080/oauth/token?username=fox&password=123456&grant_type=password&client_id=client&client_secret=123123&scope=all
         *
         *  客户端模式
         *  http://localhost:8080/oauth/token?grant_type=client_credentials&scope=all&client_id=client&client_secret=123123
         *
         *  刷新令牌
         *  http://localhost:8080/oauth/token?grant_type=refresh_token&client_id=client&client_secret=123123&refresh_token=[refresh_token值]
         */
        clients.jdbc(this.dataSource).clients(this.clientDetails());
        //        clients.inMemory()
//                //配置client_id
//                .withClient("client")
//                //配置client-secret
//                .secret(passwordEncoder.encode("123123"))
//                //配置访问token的有效期
//                .accessTokenValiditySeconds(3600)
//                //配置刷新token的有效期
//                .refreshTokenValiditySeconds(864000)
//                //配置redirect_uri，用于授权成功后跳转
//                .redirectUris("http://www.baidu.com")
//                //配置申请的权限范围
//                .scopes("all")
//                /**
//                 * 配置grant_type，表示授权类型
//                 * authorization_code: 授权码模式
//                 * implicit: 简化模式
//                 * password： 密码模式
//                 * client_credentials: 客户端模式
//                 * refresh_token: 更新令牌
//                 */
//                .authorizedGrantTypes("authorization_code","implicit","password","client_credentials","refresh_token");

    }
}
