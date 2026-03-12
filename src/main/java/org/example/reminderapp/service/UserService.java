package org.example.reminderapp.service;

import org.example.reminderapp.dto.CreateUserRequest;
import org.example.reminderapp.dto.ReminderDto;
import org.example.reminderapp.dto.UpdateUserRequest;
import org.example.reminderapp.dto.UserDto;
import org.example.reminderapp.model.User;
import org.example.reminderapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDto createUser(CreateUserRequest request){
        User user = new User();

        user.setEmail(request.getEmail());
        user.setTelegramChatId(request.getTelegramChatId());
        user.setPassword(request.getPassword());

        User savedUser = userRepository.save(user);

        UserDto dto = new UserDto();
        dto.setId(savedUser.getId());
        dto.setEmail(savedUser.getEmail());
        dto.setTelegramChatId(savedUser.getTelegramChatId());

        return dto;
    }

    public UserDto getUserById(Long id){
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()){
            User user = optionalUser.get();

            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setTelegramChatId(user.getTelegramChatId());

            return dto;
        } else {
            throw new RuntimeException("Пользователь с id " + id + " не найден");
        }
    }

    public List<UserDto> getAllUsers(){
        List<User> users = userRepository.findAll();

        List<UserDto> userDtos = new ArrayList<>();

        for (User user: users){
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setTelegramChatId(user.getTelegramChatId());

            userDtos.add(dto);
        }

        return userDtos;
    }

    public UserDto updateUser(Long id, UpdateUserRequest request){
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()){
//      проверяем опциональные поля (request == null) - поле не изменится

            User user = optionalUser.get();


            if (request.getEmail() != null){
                user.setEmail(request.getEmail());
            }
            if (request.getTelegramChatId() != null){
                user.setTelegramChatId(request.getTelegramChatId());
            }

            User updatedUser = userRepository.save(user);

            UserDto dto = new UserDto();
            dto.setEmail(updatedUser.getEmail());
            dto.setTelegramChatId(updatedUser.getTelegramChatId());

            return dto;
        } else throw new RuntimeException("Пользователь с таким id " + id + " не найден");
    }

    public void deleteUser(Long id){
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            userRepository.delete(user);
        } else throw new RuntimeException("Пользователя с таким id " + id + " не найдено");
    }


}
