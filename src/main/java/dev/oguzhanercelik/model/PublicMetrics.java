package dev.oguzhanercelik.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicMetrics {

    private int followers_count;
    private int following_count;
    private int tweet_count;
    private int listed_count;

}
