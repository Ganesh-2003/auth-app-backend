package com.app.auth.auth_app_backend.services.Impl;

import com.app.auth.auth_app_backend.dtos.UserDto;
import com.app.auth.auth_app_backend.entities.Provider;
import com.app.auth.auth_app_backend.entities.User;
import com.app.auth.auth_app_backend.exceptions.ResourceNotFoundException;
import com.app.auth.auth_app_backend.helpers.UserHelper;
import com.app.auth.auth_app_backend.repositories.UserRepository;
import com.app.auth.auth_app_backend.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDto createUser(UserDto userDto) {

        if (userDto.getEmail() == null || userDto.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        //If any extra check provide here
        User user = modelMapper.map(userDto, User.class);
        user.setProvider(userDto.getProvider() != null ? userDto.getProvider() : Provider.LOCAL);
        //Role Assignment for new user for authorization
        //TODO
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with given mail Id"));
        //Make it Dto again
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {
        UUID Id = UserHelper.parseUUID(userId);
        User existingUser =  userRepository.findById(Id).orElseThrow(() -> new ResourceNotFoundException("User not found with given userId"));

        //Email id can't be updated
        if(userDto.getName() != null)
            existingUser.setName(userDto.getName());

        if(userDto.getImage() != null)
            existingUser.setImage(userDto.getImage());

        if(userDto.getProvider() != null)
            existingUser.setProvider(userDto.getProvider());

        //Make changes to Password update logic
        if(userDto.getPassword() != null)
            existingUser.setPassword(userDto.getPassword());

        existingUser.setEnable(userDto.isEnable());
        userRepository.save(existingUser);
        return modelMapper.map(existingUser, UserDto.class);
    }

    @Override
    public void deleteUser(String userId) {
        UUID id = UserHelper.parseUUID(userId);
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with given user Id"));
        userRepository.delete(user);
    }

    @Override
    public UserDto getUserById(String userId) {
        UUID id = UserHelper.parseUUID(userId);
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with given user Id"));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public Iterable<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();
    }
}