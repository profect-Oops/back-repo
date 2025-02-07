package org.oops.global.config.auth.dto;

import lombok.Builder;
import lombok.Getter;
import org.oops.domain.user.Role;
import org.oops.domain.user.User;

import java.util.Map;

@Getter
public class OAuthAttributes {
    //OAuth2 인증 과정에서 반환된 사용자 정보를 다루기 위한 클래스
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String userName;
    private String email;
    private String phoneNumber;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes,
                           String nameAttributeKey,
                           String userName,
                           String email,
                           String phoneNumber) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    //OAuth2User에서 반환하는 사용자 정보는 Map
    //따라서 값 하나하나를 변환해야 함
    public static OAuthAttributes of(String registrationId,
                                     String userNameAttributeName,
                                     Map<String, Object> attributes) {
        return ofGoogle(userNameAttributeName, attributes);
    }

    //구글 생성자
    private static OAuthAttributes ofGoogle(String usernameAttributeName,
                                            Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .userName((String) attributes.get("nameName"))
                .email((String) attributes.get("email"))
                .phoneNumber((String) attributes.get("phoneNumber"))
                .attributes(attributes)
                .nameAttributeKey(usernameAttributeName)
                .build();
    }

    //User Entity 생성
    public User toEntity(){
        return User.builder()
                .userName(userName)
                .email(email)
                .phoneNumber(phoneNumber)
                .role(Role.USER)
                .build();
    }
}
