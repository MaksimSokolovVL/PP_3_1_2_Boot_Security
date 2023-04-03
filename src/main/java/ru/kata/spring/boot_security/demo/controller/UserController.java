package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Set;

@Controller
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/addNewUser")
    public String addNewUser(Model model) {

        User tempUser = (User) model.getAttribute("tempUser");

        User user = tempUser == null ? new User() : tempUser;


        Set<Role> allDistinctRoles = roleService.findAllDistinctRoles();

        model.addAttribute("edit_user", user);
        model.addAttribute("allRoles", allDistinctRoles);

        return "add_user";
    }

    @PostMapping("/saveUser")
    public String saveUser(@Valid @ModelAttribute("edit_user") User user,
                           BindingResult bindingResult, Model model) {


        User UserFindByUsername = userService.findByUsername(user.getUsername());
        if (UserFindByUsername != null) {
            model.addAttribute("user_message", "Login exists!");
            model.addAttribute("tempUser", user);
            return addNewUser(model);
        }
        if (bindingResult.hasErrors()) {
            return "redirect:/add_user";
        }


        user.setActive(true);

        if (user.getRoles().size() == 0 || user.getRoles() == null) {
            user.setRoles(Collections.singleton(new Role("ROLE_USER")));
        }
        passwordEncoder(user);

        userService.saveAndUpdateUser(user);

        return "redirect:/admin";
    }


    @PostMapping("/updateInfo")
    public String updateUserInfo(@RequestParam("empId") long id, Model model) {

        model.addAttribute("edit_user", userService.getUserForId(id));
        model.addAttribute("allRoles", roleService.findAllDistinctRoles());
        return "edit-user";
    }

    @PostMapping("/update_user")
    public String updateUser(@ModelAttribute("edit_user") User updateUser, BindingResult result) {

        if (result.hasErrors()) {
            return "edit-user";
        }

        passwordEncoder(updateUser);
        userService.saveAndUpdateUser(updateUser);

        return "redirect:/admin";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(@RequestParam("empId") long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    private void passwordEncoder(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
}
