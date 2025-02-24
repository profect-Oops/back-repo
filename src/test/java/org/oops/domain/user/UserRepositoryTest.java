package org.oops.domain.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void findByEmail() {
        //given
        String existingEmail = "test@test.com";
        User user = User.builder()
                .userName("user1")
                .email(existingEmail)
                .phoneNumber("01012341234")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(user));

        //when
        Optional<User> result = userRepository.findByEmail(existingEmail);

        //then
        assertEquals(user.getEmail(), result.orElseThrow().getEmail());

    }
}