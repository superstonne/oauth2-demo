package com.nick.microservice.client.client.controller;

import com.nick.microservice.client.client.model.User;
import com.nick.microservice.client.client.oauth.CodeTokenService;
import com.nick.microservice.client.client.oauth.OAuth2Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class MainController {
    @Autowired
    private CodeTokenService tokenService;

    private ConcurrentHashMap<String, OAuth2Token> userStore = new ConcurrentHashMap<>();

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/client/token/code")
    public ModelAndView callback(String code, String state) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        OAuth2Token token = tokenService.getToken(code);

        userStore.put(userDetails.getUsername(), token);

        return new ModelAndView("redirect:/mainpage");
    }

    @GetMapping("/mainpage")
    public ModelAndView mainpage() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        /**
         * 客户代理试图从缓存中获取资源拥有者颁发的授权 Token，若未找到或者已经过期，
         * 则需要跳转到授权请求服务重新获取授权
         */
        OAuth2Token token = userStore.get(userDetails.getUsername());
        if (token == null) {
            String authEndpoint = tokenService.getAuthorizationEndpoint();
            return new ModelAndView("redirect:" + authEndpoint);
        }

        ModelAndView mv = new ModelAndView("mainpage");
        User user = accessUserResource(token.getAccessToken());
        mv.addObject("userInfo", user);
        return mv;
    }

    /**
     *  客户代理携带 Token 访问受保护资源
     * @param token
     * @return
     */
    private User accessUserResource(String token) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + token);
        String endpoint = "http://localhost:7070/api/user";
        RequestEntity<Object> request = new RequestEntity<>(
                headers, HttpMethod.GET, URI.create(endpoint));

        ResponseEntity<User> userInfo = restTemplate.exchange(request, User.class);

        if (!userInfo.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("请求资源异常");
        }
        return userInfo.getBody();
    }

}
