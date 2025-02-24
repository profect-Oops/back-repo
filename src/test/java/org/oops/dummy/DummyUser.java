package org.oops.dummy;

import org.oops.domain.user.Role;
import org.oops.domain.user.User;

import java.lang.reflect.Field;

public class DummyUser {
    public static User createDummyUser(String userName, String phoneNumber, String email, Role role) {
        return new User(userName, phoneNumber, email, role);
    }

    //다른 도메인에서 user값이 있는 필드를 지울 때 userId를 확인하므로 userId값을 set 해주어야 하는데
    //이때 setter 주입을 피하기 위해 reflection을 사용해서 userId를 설정할 수 있다
    public static User deleteDummyUser(Long id, String userName, String email, String phoneNumber, Role role) {
        User user = new User(userName, phoneNumber, email, role);
        setId(user, id);
        return user;
    }

    public static void setId(User user, Long id) {
        try{
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        }catch (NoSuchFieldException | IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }
}

