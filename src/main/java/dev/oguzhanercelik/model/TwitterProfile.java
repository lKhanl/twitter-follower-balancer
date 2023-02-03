package dev.oguzhanercelik.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitterProfile {

    private String id;
    private String name;
    private String username;
    private String description;
    private String profile_image_url;
    private PublicMetrics public_metrics;

}