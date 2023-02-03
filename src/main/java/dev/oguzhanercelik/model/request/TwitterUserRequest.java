package dev.oguzhanercelik.model.request;

import dev.oguzhanercelik.model.dto.TwitterUserDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TwitterUserRequest {

    private List<TwitterUserDto> data;
    private TwitterMeta meta;

}
