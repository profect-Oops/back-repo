package org.oops.api.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.oops.domain.user.Role;
import org.oops.domain.user.User;

@Getter
@NoArgsConstructor
public class UserDTO {

    private String userName;

    private String phoneNumber;

    private String email;

    private Role role;

    @Builder
    public UserDTO(String userName, String phoneNumber, String email, Role role) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
    }

    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .userName(user.getUserName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
