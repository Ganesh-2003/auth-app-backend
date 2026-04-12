package com.app.auth.auth_app_backend.services;

import com.app.auth.auth_app_backend.dtos.UserDto;

public interface UserService {

    //Create User
    UserDto createUser(UserDto userDto);

    //get User By Email
    UserDto getUserByEmail(String email);

    //update User
    UserDto updateUser(UserDto userDto, String userId);

    //delete User
    void deleteUser(String userId);

    //get user by id
    UserDto getUserById(String userId);

    //Get all users
    Iterable<UserDto> getAllUsers();
}
