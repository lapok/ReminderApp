package org.example.reminderapp.service;

import lombok.RequiredArgsConstructor;
import org.example.reminderapp.dto.UserDto;
import org.example.reminderapp.mapper.UserMapper;
import org.example.reminderapp.model.User;
import org.example.reminderapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    @Transactional
    public UserDto createUser(UserDto dto){
        User user = userMapper.toEntity(dto);

        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userMapper.toDto(userRepository.save(user));
    }

    public UserDto getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return userMapper.toDto(user);
    }

    public List<UserDto> getAllUsers(){
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto dto){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            if (dto.getEmail() != null){
                user.setEmail(dto.getEmail());
            }
            if (dto.getTelegramChatId() != null){
                user.setTelegramChatId(dto.getTelegramChatId());
            }

            return userMapper.toDto(userRepository.save(user));


    }

    @Transactional
    public void deleteUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        userRepository.delete(user);
    }


}
