package org.oops.api.user.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.oops.api.user.dto.UserDTO;
import org.oops.domain.user.User;
import org.oops.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAll(){
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOS = users.stream()
                .map(UserDTO::fromEntity)
                .toList();
        return userDTOS;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User가 존재하지 않습니다.")
        );

        return UserDTO.fromEntity(user);
    }

    @Override
    public UserDTO update(Long userId, UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user = user.update(userDTO.getUserName(), userDTO.getPhoneNumber());

            return UserDTO.fromEntity(user);
        }else{
            throw new IllegalArgumentException("해당 아이디를 가진 user가 없습니다: "+userId);
        }
    }
}
