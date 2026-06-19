package org.example.expert.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSearchResponse {
    private Long id;
    private String email;
    private String nickname;
}
