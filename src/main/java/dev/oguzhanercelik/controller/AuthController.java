package dev.oguzhanercelik.controller;

import dev.oguzhanercelik.model.Singleton;
import dev.oguzhanercelik.model.dto.TwitterUserDto;
import dev.oguzhanercelik.model.entity.MailConfirmation;
import dev.oguzhanercelik.model.entity.TwitterUser;
import dev.oguzhanercelik.model.entity.User;
import dev.oguzhanercelik.model.request.CreateUserRequest;
import dev.oguzhanercelik.model.request.TwitterProfileRequest;
import dev.oguzhanercelik.service.ConfirmationService;
import dev.oguzhanercelik.service.MailService;
import dev.oguzhanercelik.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AuthController {

    @Value("${twitter.bearer}")
    private String bearer;
    @Value("${twitter.api.url}")
    private String base;
    private final UserService userService;
    private final MailService mailService;
    private final ConfirmationService confirmationService;

    @GetMapping("")
    public String viewHomePage() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        return "signup_form";
    }

    @PostMapping("/process_register")
    public String processRegister(CreateUserRequest body) throws MessagingException, IOException {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Map<String, String> map = Map.of("Authorization", "Bearer " + bearer);
        String responseBody = Singleton.getHttpResponseBody(null, null, base + "users/by/username/" + body.getUsername(), "GET", map);
        TwitterProfileRequest twitterUserDto = (TwitterProfileRequest) Singleton.getPOJOFromJSON(responseBody, TwitterProfileRequest.class);
        if (twitterUserDto == null) {
            return "redirect:/register";
        }

        User user = new User();
        user.setEmail(body.getEmail());
        user.setPassword(passwordEncoder.encode(body.getPassword()));
        user.setEnabled(false);
        user.setTwitterId(twitterUserDto.getData().getId());
        userService.save(user);

        MailConfirmation mailConfirmation = new MailConfirmation();
        mailConfirmation.setMail(user.getEmail());
        mailConfirmation.setToken(Singleton.generateRandomString(20));
        confirmationService.save(mailConfirmation);

        Map<String, Object> content = Map.of("link", "https://twitter-balancer.oguzhanercelik.dev/confirm?token=" + mailConfirmation.getToken() + "&mail=" + user.getEmail());
        mailService.sendMail(user.getEmail(), "Confirmation Mail", "ConfirmationMail", content);

        return "redirect:/login";
    }

    @GetMapping("/confirm")
    public String confirm(@RequestParam String token, @RequestParam String mail) throws Exception {
        MailConfirmation mailConfirmation = confirmationService.findByTokenAndMail(token, mail);
        if (mailConfirmation != null) {
            User user = userService.findByEmail(mail);
            user.setEnabled(true);
            userService.save(user);
            return "redirect:/login";
        }
        return "redirect:/";
    }

}
