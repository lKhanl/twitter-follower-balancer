package dev.oguzhanercelik.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.oguzhanercelik.model.TwitterStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitterUserDto {

    private String id;
    private String name;
    private String username;
    private Boolean verified;
    private TwitterStatus status;
}
