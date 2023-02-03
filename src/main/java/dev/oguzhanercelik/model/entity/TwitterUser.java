package dev.oguzhanercelik.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.oguzhanercelik.model.TwitterStatus;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "twitter_users")
public class TwitterUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonProperty("id")
    @Column(nullable = false)
    private String twitter_id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private Boolean verified;
    private TwitterStatus status;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public TwitterUser(String twitter_id, String name, String username, TwitterStatus status, Boolean verified, User user) {
        this.twitter_id = twitter_id;
        this.name = name;
        this.username = username;
        this.user = user;
        this.status = status;
        this.verified = verified;
    }
}
