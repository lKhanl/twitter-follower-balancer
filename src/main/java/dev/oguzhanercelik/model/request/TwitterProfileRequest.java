package dev.oguzhanercelik.model.request;

import dev.oguzhanercelik.model.TwitterProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TwitterProfileRequest {

    private TwitterProfile data;

}
