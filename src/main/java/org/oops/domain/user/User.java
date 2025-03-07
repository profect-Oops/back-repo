package org.oops.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@JsonIgnoreProperties(ignoreUnknown = true)  // roleKey 같은 불필요한 필드를 무시
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "PHONENUMBER")
    private String phoneNumber;

    @Column(nullable = false, name = "USER_EMAIL")
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(String userName, String phoneNumber, String email, Role role) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
    }

    public User update(String userName, String phoneNumber) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getRoleKey(){
        return this.role.getKey();
    }

}
