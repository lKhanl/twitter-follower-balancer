package dev.oguzhanercelik.service;

import dev.oguzhanercelik.model.Singleton;
import dev.oguzhanercelik.model.TwitterProfile;
import dev.oguzhanercelik.model.TwitterStatus;
import dev.oguzhanercelik.model.entity.TwitterUser;
import dev.oguzhanercelik.model.entity.User;
import dev.oguzhanercelik.model.request.TwitterProfileRequest;
import dev.oguzhanercelik.model.request.TwitterProfilesRequest;
import dev.oguzhanercelik.repository.TwitterUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TwitterService {

    @Value("${twitter.api.url}")
    private String base;
    @Value("${twitter.bearer}")
    private String token;
    private final TwitterUserRepository twitterUserRepository;

    public Optional<TwitterUser> findByUsernameOptional(String username, User user) {
        return twitterUserRepository.findByUsernameAndUser(username, user);
    }

    public TwitterUser save(TwitterUser twitterUser) {
        return twitterUserRepository.save(twitterUser);
    }

    public List<TwitterUser> findByUserAndStatus(User u, TwitterStatus status) {
        return twitterUserRepository.findByUserAndStatus(u, status);
    }

    public TwitterProfile getTwitterProfile(User user) throws IOException {
        Map<String, String> map = Map.of("Authorization", "Bearer " + token);
        String res = Singleton.getHttpResponseBody(null, null, base + "users/" + user.getTwitterId() +
                "?user.fields=description,entities,name,profile_image_url,public_metrics,username", "GET", map);
        TwitterProfileRequest json = (TwitterProfileRequest) Singleton.getPOJOFromJSON(res, TwitterProfileRequest.class);
        return json.getData();
    }

    public List<TwitterUser> getUnfollowList(List<TwitterUser> twitterUsers) throws IOException {
        Map<String, String> map = Map.of("Authorization", "Bearer " + token);
        String twitterId = twitterUsers.stream().map(TwitterUser::getTwitter_id).reduce((a, b) -> a + "," + b).get();
        String res = Singleton.getHttpResponseBody(null, null, base + "users?ids=" + twitterId +
                "&user.fields=public_metrics", "GET", map);

        TwitterProfilesRequest json = (TwitterProfilesRequest) Singleton.getPOJOFromJSON(res, TwitterProfilesRequest.class);
        List<TwitterProfile> list = json.getData();
        List<TwitterUser> list2 = new LinkedList<>();

        for (TwitterProfile profile : list) {
            if (profile.getPublic_metrics().getFollowers_count() < 10000) {
                TwitterUser twitterUser = new TwitterUser();
                twitterUser.setName(profile.getName());
                twitterUser.setUsername(profile.getUsername());
                twitterUser.setTwitter_id(profile.getId());
                twitterUser.setStatus(TwitterStatus.FOLLOW);
                twitterUser.setUser(twitterUsers.get(0).getUser());
                list2.add(twitterUser);
            }
        }
        return list2;
    }

    public void deleteByUser(User u) {
        twitterUserRepository.deleteByUser(u);
    }
}
