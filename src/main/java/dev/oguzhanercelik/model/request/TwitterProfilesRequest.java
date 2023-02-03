package dev.oguzhanercelik.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.oguzhanercelik.model.TwitterProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitterProfilesRequest {

    private List<TwitterProfile> data;

}
