package com.nick.microservice.account.config;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.client.RestOperations;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * @description:
 * @author: 肖奈（贺金龙）
 * @create: 2019-08-10 17:29
 */
@Configuration
@EnableResourceServer
public class ResourceServer extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/**").authenticated();
    }

//    @Bean
//    public ResourceServerTokenServices tokenServices() {
//        RemoteTokenServices tokenServices = new RemoteTokenServices();
//        tokenServices.setCheckTokenEndpointUrl("http://localhost:8080/oauth/check_token");
//        tokenServices.setClientId("client");
//        tokenServices.setClientSecret("secret");
//        tokenServices.setTokenName("token");
//        return tokenServices;
//    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("123");
        return converter;
    }
    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    @Bean
    public OAuth2AuthenticationManager authenticationManager() {
        OAuth2AuthenticationManager manager = new OAuth2AuthenticationManager();
        manager.setTokenServices(tokenServices());
        return  manager;
    }
}
