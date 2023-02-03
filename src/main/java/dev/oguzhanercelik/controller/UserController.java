package dev.oguzhanercelik.controller;

import dev.oguzhanercelik.model.TwitterProfile;
import dev.oguzhanercelik.model.entity.User;
import dev.oguzhanercelik.service.BalancerService;
import dev.oguzhanercelik.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final BalancerService balancerService;

    @GetMapping("/api/users/profile")
    public String profile(Principal principal, HttpServletRequest request) throws Exception {
        User user = userService.findByEmail(principal.getName());
        request.setAttribute("user", user);
        return "profile";
    }

    @PostMapping(value = "/api/users/update", consumes = "application/x-www-form-urlencoded")
    public String updateUser(@RequestParam(required = false) String email, @RequestParam(required = false) String password,
                             @RequestParam String twitterId, Principal principal) throws Exception {

        User user = userService.findByEmail(principal.getName());
        System.out.println(user);
        if (email != null && !email.isEmpty() && !email.equals(user.getEmail())) {
            user.setEmail(email);
        }
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        if (twitterId != null && !twitterId.isEmpty()) {
            user.setTwitterId(twitterId);
        }
        userService.save(user);

        return "redirect:/profile";
    }

    @PostMapping("/api/users/delete")
    public String deleteUser(Principal principal) throws Exception {
        User user = userService.findByEmail(principal.getName());
        balancerService.reset(user);
        userService.delete(user.getId());
        return "redirect:/logout";
    }

}
