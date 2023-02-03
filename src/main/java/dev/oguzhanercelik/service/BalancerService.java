package dev.oguzhanercelik.service;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsOAuth1;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.UsersFollowingDeleteResponse;
import dev.oguzhanercelik.config.Configuration;
import dev.oguzhanercelik.model.Singleton;
import dev.oguzhanercelik.model.TwitterStatus;
import dev.oguzhanercelik.model.dto.TwitterUserDto;
import dev.oguzhanercelik.model.entity.TwitterUser;
import dev.oguzhanercelik.model.entity.User;
import dev.oguzhanercelik.model.request.TwitterUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalancerService {

    @Value("${twitter.api.url}")
    private String base;
    @Value("${twitter.bearer}")
    private String bearer;
    @Value("${twitter.consumer.key}")
    private String consumerKey;
    @Value("${twitter.consumer.secret}")
    private String consumerSecret;
    @Value("${twitter.token}")
    private String token;
    @Value("${twitter.tokenSecret}")
    private String tokenSecret;
    private final Configuration configuration;
    private final TwitterService twitterService;
    private final MailService mailService;
    private final UserService userService;

    private void getTwitterFollowers(User u) throws Exception {
        Map<String, String> map = Map.of("Authorization", "Bearer " + bearer);

        String res = Singleton.getHttpResponseBody(null, null, base + "users/" + u.getTwitterId() + "/followers?max_results=1000&user.fields=verified", "GET", map);
        TwitterUserRequest json = (TwitterUserRequest) Singleton.getPOJOFromJSON(res, TwitterUserRequest.class);

        saveTwitterUser(json.getData(), u, TwitterStatus.FOLLOW);

        if (json.getMeta().getNext_token() != null) {
            String nextToken = json.getMeta().getNext_token();
            while (nextToken != null) {
                log.info("Waiting 5 seconds");
                Thread.sleep(5000);
                res = Singleton.getHttpResponseBody(null, null, base + "users/" + u.getTwitterId() + "/followers?max_results=1000&user.fields=verified&pagination_token=" + nextToken, "GET", map);
                json = (TwitterUserRequest) Singleton.getPOJOFromJSON(res, TwitterUserRequest.class);
                saveTwitterUser(json.getData(), u, TwitterStatus.FOLLOW);
                nextToken = json.getMeta().getNext_token();
            }
        }
    }

    private void saveTwitterUser(List<TwitterUserDto> data, User u, TwitterStatus status) {
        int i = 1;
        for (TwitterUserDto user : data) {
            Optional<TwitterUser> optional = twitterService.findByUsernameOptional(user.getUsername(), u);

            if (optional.isEmpty()) {
                TwitterUser twitterUser = new TwitterUser(user.getId(), user.getName(), user.getUsername(), status, user.getVerified(), u);
                twitterService.save(twitterUser);
                log.info(i + ". Twitter User Saved: " + twitterUser.getUsername() + " " + twitterUser.getStatus());
            } else {
                TwitterUser twitterUser = optional.get();
                if (twitterUser.getStatus() == TwitterStatus.FOLLOW || status == TwitterStatus.FOLLOWING) {
                    twitterUser.setStatus(TwitterStatus.BOTH);
                    twitterService.save(twitterUser);
                    log.info(i + ". Twitter User Updated: " + twitterUser.getUsername() + " " + twitterUser.getStatus());
                } else if (twitterUser.getStatus() == TwitterStatus.BOTH) {
                    log.info(i + ". Twitter User Already Exist: " + twitterUser.getUsername() + " " + twitterUser.getStatus());
                }
            }
            i++;
        }
    }

    private void getTwitterFollowings(User u) throws Exception {
        Map<String, String> map = Map.of("Authorization", "Bearer " + bearer);

        String res = Singleton.getHttpResponseBody(null, null, base + "users/" + u.getTwitterId() + "/following?max_results=1000&user.fields=verified", "GET", map);
        TwitterUserRequest json = (TwitterUserRequest) Singleton.getPOJOFromJSON(res, TwitterUserRequest.class);

        saveTwitterUser(json.getData(), u, TwitterStatus.FOLLOWING);

        if (json.getMeta().getNext_token() != null) {
            String nextToken = json.getMeta().getNext_token();
            while (nextToken != null) {
                log.info("Waiting 5 seconds");
                Thread.sleep(5000);
                res = Singleton.getHttpResponseBody(null, null, base + "users/" + u.getTwitterId() + "/following?max_results=1000&user.fields=verified&pagination_token=" + nextToken, "GET", map);
                json = (TwitterUserRequest) Singleton.getPOJOFromJSON(res, TwitterUserRequest.class);
                saveTwitterUser(json.getData(), u, TwitterStatus.FOLLOWING);
                nextToken = json.getMeta().getNext_token();
            }
        }
    }

    @Async
    public void getTwitterFollowersAndFollowings(User u) throws Exception {
        if (!configuration.isEnabled()) {
            log.info("Twitter Followers and Followings are disabled");
            throw new Exception("Twitter Followers and Followings are disabled");
        }
        if (u.getLastProcessDate() != null && u.getLastProcessDate().plusMonths(1).isAfter(LocalDate.now())) {
            log.info("Twitter Followers and Followings are already processed this month");
            throw new Exception("Limit Reached");
        }
        configuration.setEnabled(false);

        reset(u);

        getTwitterFollowers(u);
        getTwitterFollowings(u);
        balance(u);
        log.info("Finished");
        configuration.setEnabled(true);

        u.setLastProcessDate(LocalDate.now());
        userService.save(u);
    }

    public void balance(User u) throws IOException, MessagingException {
        // lis of users that are follow them but they ara not following me
        List<TwitterUser> unfollows = twitterService.findByUserAndStatus(u, TwitterStatus.FOLLOWING);
        List<TwitterUser> filtered = new LinkedList<>();

        for (int i = 0; i < unfollows.size(); i += 100) {
            List<TwitterUser> subList = unfollows.subList(i, Math.min(unfollows.size(), i + 100));
            List<TwitterUser> list = new LinkedList<>();
            for (TwitterUser twitterUser : subList) {
                if (!twitterUser.getVerified()) {
                    list.add(twitterUser);
                }
            }
            if (list.size() > 0) {
                List<TwitterUser> unfollowList = twitterService.getUnfollowList(list);
                filtered.addAll(unfollowList);
            }
        }

        System.out.println("Unfollows: " + filtered.size());

        mailService.sendMail(u.getEmail(), "Unfollow List", "UnfollowMail", Map.of("unfollows", filtered, "name", u.getUsername()));
    }

    public void reset(User u) {
        twitterService.deleteByUser(u);
    }
}

