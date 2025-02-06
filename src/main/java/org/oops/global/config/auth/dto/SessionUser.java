package org.oops.global.config.auth.dto;

import lombok.Getter;
import org.oops.domain.user.User;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {

    // 세션에 저장할 인증된 사용자 정보를 담기 위한 클래스
    // 인증된 사용자 정보만 필요 => userName, email, phoneNumber 필드만 선언
    private Long id;
    private String userName;
    private String email;
    private String phoneNumber;

    public SessionUser(User user){
        this.id = user.getUserId();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
    }
}
