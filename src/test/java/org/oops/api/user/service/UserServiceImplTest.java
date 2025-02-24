package org.oops.api.user.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.oops.api.user.dto.UserDTO;
import org.oops.domain.user.Role;
import org.oops.domain.user.User;
import org.oops.domain.user.UserRepository;
import org.oops.dummy.DummyUser;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private User user1, user2;

    @BeforeEach
    void setUp() {
        user1 = DummyUser.createDummyUser("user1", "01012341234", "user1@example.com", Role.USER);
        user2 = DummyUser.createDummyUser("user2", "01098765432", "user2@example.com", Role.USER);
    }

    @Test
    void findAll() {
        List<User> userList = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(userList);

        List<UserDTO> userDTOList = userServiceImpl.findAll();

        assertEquals(2, userDTOList.size());
    }

    @Test
    void findById_ExistingUser() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        UserDTO userDTO = userServiceImpl.findById(userId);

        assertEquals(user1.getUserName(), userDTO.getUserName());
    }

    @Test
    void findById_NonExistingUser() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userServiceImpl.findById(userId));
    }

    @Test
    void update_ExistingUser() {
        Long userId = 2L;
        UserDTO updateUserDTO = UserDTO.builder()
                .userName("user02")
                .phoneNumber("01011111111")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));

        UserDTO result = userServiceImpl.update(userId, updateUserDTO);

        assertEquals(updateUserDTO.getUserName(), result.getUserName());
        assertEquals(updateUserDTO.getPhoneNumber(), result.getPhoneNumber());
    }

    @Test
    void update_NonExistingUser() {
        Long userId = 2L;
        UserDTO updateUserDTO = UserDTO.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userServiceImpl.update(userId, updateUserDTO));

    }
}