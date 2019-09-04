package com.nick.microservice.account.api;

import com.nick.microservice.account.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:
 * @author: 肖奈（贺金龙）
 * @create: 2019-08-10 17:06
 */
@Controller
public class UserController {

    @GetMapping("/api/user")
    public ResponseEntity<User> getUserInfo() {

        User userInfo = new User();
        userInfo.setName("user001");
        userInfo.setMail("user001@email.com");

        return ResponseEntity.ok(userInfo);
    }
}
