package web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import web.model.User;
import web.service.UserService;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String showMainPage() {
        return "index";
    }

    @GetMapping("/users")
    public String showAllUsers(ModelMap model) {
        model.addAttribute("users", userService.getAllUser());
        return "users";
    }

    @GetMapping("/users/add")
    public String showNewUserForm(ModelMap model) {
        model.addAttribute("user", new User());
        return "addUser";
    }

    @PostMapping("/users")
    public String addUser(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/users";
    }

    @GetMapping("/users/update/{id}")
    public String updateUserForm(@PathVariable("id") Long id, Model model) {
        User userFromDB = userService.findById(id);
        model.addAttribute("user", userFromDB);
        return "updateUser";
    }

    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/users";
    }

    @GetMapping("/users/remove/{id}")
    public String removeUserById(@PathVariable("id") Long id) {
        User userFromDB = userService.findById(id);
        userService.removeUser(userFromDB);
        return "redirect:/users";
    }
}
