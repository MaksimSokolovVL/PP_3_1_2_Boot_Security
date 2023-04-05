package ru.kata.spring.boot_security.demo.controller;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping
    public String getUserPage(Model model, Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        List<String> roles = new ArrayList<>();
        for (Role role : user.getRoles()) {
            roles.add(role.getRoleName().replace("ROLE_", ""));
        }

        String joinRoles = String.join(", ", roles);
        model.addAttribute("rolesList", joinRoles);
        model.addAttribute("userIn", user);
        return "user";
    }
}