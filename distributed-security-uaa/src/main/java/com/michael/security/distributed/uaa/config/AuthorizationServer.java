package com.michael.security.distributed.uaa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * 授权服务配置
 *
 * @author wangce 2021-05-20
 * @since 1.0.0
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtAccessTokenConverter accessTokenConverter;

    @Autowired
    private PasswordEncoder passwordEncoder;


    //1.客户端详情相关配置
    // 将客户端信息存储到数据库
    @Bean
    public ClientDetailsService clientDetailsService(DataSource dataSource) {
        ClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
        ((JdbcClientDetailsService)clientDetailsService).setPasswordEncoder(passwordEncoder);
        return clientDetailsService;
    }

    /**
     * 配置客户端详细信息服务
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);
        //内存方式
        /*clients.inMemory()//使用in-memory存储
                .withClient("c1")//cliend_id
                .secret(new BCryptPasswordEncoder().encode("secret"))//client密钥
                .resourceIds("res1")//客户端访问的资源列表
                .authorizedGrantTypes("authorization_code","password",
                        "client_credentials","implicit","refresh_token")//该client允许的授权类型
                .scopes("all")//允许的授权范围
                .autoApprove(false)//false跳转到授权的页面 true 直接发令牌
                //加上验证回调地址
                .redirectUris("http://www.baidu.com");*/
    }

    /**
     * 2.配置令牌服务
     * @return
     */
    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        DefaultTokenServices service = new DefaultTokenServices();
        service.setClientDetailsService(clientDetailsService);//客户端服务信息
        service.setSupportRefreshToken(true);//是否支持刷新令牌
        service.setTokenStore(tokenStore);//令牌存储策略
        //令牌增强
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(accessTokenConverter));
        service.setTokenEnhancer(tokenEnhancerChain);

        service.setAccessTokenValiditySeconds(7200);//令牌默认有效期2小时
        service.setRefreshTokenValiditySeconds(259200);//刷新令牌默认有效期3天
        return service;
    }

    /*@Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        //设置授权码模式的授权码如何存取，暂时采用内存方式
        return new InMemoryAuthorizationCodeServices();
    }*/

    /**
     * 3.配置令牌（token）的访问端点
     *
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource) {
        //设置授权码模式的授权码如何存取
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    /**
     * 令牌访问端点
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager)//密码模式需要
                .authorizationCodeServices(authorizationCodeServices)//授权码模式需要
                .tokenServices(tokenServices())//令牌管理服务
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);//允许post提交
    }

    /**
     * 4.配置令牌端点（Token Endpoint）安全约束
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")///oauth/token_key公开
                .checkTokenAccess("permitAll()")///oauth/check_token 公开
                .allowFormAuthenticationForClients();//表单认证，申请令牌
    }
}
