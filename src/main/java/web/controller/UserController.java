package web.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import web.model.Role;
import web.model.User;
import web.service.RoleService;
import web.service.UserService;

import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/index")
    public String indexPage() {
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
    public String addUser(@ModelAttribute("user") User user, ModelMap model, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            model.addAttribute("bindingResult", bindingResult.getAllErrors());
            return "addUser";
        }

        if (userService.getAllUser()
                .stream()
                .anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
            model.addAttribute("loginError", "Login already exists, please choose another login");
            return "addUser";
        }

        if (user.getPassword() != null && !user.getPassword().equals(user.getPasswordConfirm())) {
            model.addAttribute("passwordError", "Passwords are different");
            return "addUser";
        }

        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        Role roleUser = roleService.findByName("USER");
        user.setRole(roleUser);
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/user/update/{id}")
    public String updateUserForm(@PathVariable("id") Long id, Model model) {
        User userFromDB = userService.findById(id);
        model.addAttribute("user", userFromDB);
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "updateUser";
    }

    @PostMapping("/user/update")
    public String updateUser(@ModelAttribute("user") User user, Authentication authentication,
                             @RequestParam Map<String, String> form) {
        List<Role> roles = roleService.getAllRoles();

        for (String key : form.keySet()) {
            if (roles.stream().anyMatch(role -> role.getName().equals(key))) {
                user.getRoles().add(roleService.findByName(key));
            }
        }
        userService.saveUser(user);

        if (authentication.getAuthorities()
                .stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))) {

            return "redirect:/admin";
        }
        return "redirect:/index";
    }

    @GetMapping("/user/remove/{id}")
    public String removeUserById(@PathVariable("id") Long id) {
        User userFromDB = userService.findById(id);
        userService.removeUser(userFromDB);
        return "redirect:/admin";
    }
}
