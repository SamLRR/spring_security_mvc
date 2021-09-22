package web.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import web.model.Role;
import web.model.User;
import web.service.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/index")
    public String indexPage(){
        return "index";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showAdminPage(ModelMap model) {
        model.addAttribute("users", userService.getAllUser());
        return "admin";
    }

    @GetMapping("/user")
    public String showUserPage(ModelMap model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", userService.findUserByName(user.getUsername()));
        return "user";
    }

    @GetMapping("/user/add")
    public String showNewUserForm(ModelMap model) {
        model.addAttribute("user", new User());
        return "addUser";
    }

    @PostMapping("/user")
    public String addUser(@ModelAttribute("user") User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userService.saveUser(user);
        return "admin";
    }

    @GetMapping("/user/update/{id}")
    public String updateUserForm(@PathVariable("id") Long id, Model model) {
        User userFromDB = userService.findById(id);
        model.addAttribute("user", userFromDB);
        model.addAttribute("role", userFromDB.getRoles());
        return "updateUser";
    }

    @PostMapping("/user/update")
    public String updateUser(@ModelAttribute("user") User user, Authentication authentication) {
        userService.saveUser(user);
        if (authentication.getAuthorities()
                .stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {

            return "admin";
        }
        return "index";
    }

    @GetMapping("/user/remove/{id}")
    public String removeUserById(@PathVariable("id") Long id) {
        User userFromDB = userService.findById(id);
        userService.removeUser(userFromDB);
        return "admin";
    }
}
