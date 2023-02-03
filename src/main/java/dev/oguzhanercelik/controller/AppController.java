package dev.oguzhanercelik.controller;

import dev.oguzhanercelik.model.TwitterProfile;
import dev.oguzhanercelik.model.entity.User;
import dev.oguzhanercelik.service.BalancerService;
import dev.oguzhanercelik.service.TwitterService;
import dev.oguzhanercelik.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class AppController {

    private final UserService userService;
    private final BalancerService balancerService;
    private final TwitterService twitterService;

    @GetMapping("/profile")
    public String userInfo(Principal principal, HttpServletRequest request) throws Exception {
        User user = userService.findByEmail(principal.getName());
        request.setAttribute("user", user);
        TwitterProfile profile = twitterService.getTwitterProfile(user);
        request.setAttribute("twitter_profile", profile);

        return "profile";
    }

    @GetMapping("/balance")
    public String getTwitterFollowers(Principal principal, HttpServletRequest request) throws Exception {
        User user = userService.findByEmail(principal.getName());
        request.setAttribute("user", user);
        request.setAttribute("twitter_profile", twitterService.getTwitterProfile(user));

        balancerService.getTwitterFollowersAndFollowings(user);

        return "redirect:/profile";
    }

    @GetMapping("/twitter")
    public String twitter(Principal principal, HttpServletRequest request) throws Exception {
        User user = userService.findByEmail(principal.getName());
        request.setAttribute("twitter_profile", twitterService.getTwitterProfile(user));

        return "TwitterProfile";
    }
}
