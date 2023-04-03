package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserRole;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.Collections;
import java.util.Set;

@Controller
public class RegistrationController {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;

    public RegistrationController(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model) {

        User byUsername = userRepo.findByUsername(user.getUsername());
        Set<Role> allDistinctRoles = roleRepo.findAllDistinctRoles();

        if (byUsername != null) {
            model.addAttribute("user_message", "User exists!");
            return "registration";
        }

        user.setActive(true);

        if (allDistinctRoles.size() == 0) {
            user.setRoles(Collections.singleton(createdUserRole()));
        } else {
            Role byRoleName = roleRepo.findByRoleName(UserRole.USER.getName());
            user.setRoles(Collections.singleton(byRoleName != null ? byRoleName : createdUserRole()));
        }
        user.setPassword(encoder.encode(user.getPassword()));
        userRepo.save(user);
        return "redirect:/login";
    }

    private Role createdUserRole() {
        return new Role(UserRole.USER.getName());
    }
}
