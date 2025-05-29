package com.example.pizzashop.services;

import com.example.pizzashop.dtos.ForgotPasswordDTO;
import com.example.pizzashop.dtos.UpdateUserDTO;
import com.example.pizzashop.dtos.UserDTO;
import com.example.pizzashop.models.User;
import com.example.pizzashop.responses.PizzaResponse;
import com.example.pizzashop.responses.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login (String phoneNumber, String password) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    User updateUser(Long userId, UpdateUserDTO updateUserDTO) throws Exception;
    Page<UserResponse> getAllUsers(String keyword, PageRequest pageRequest);

    void deleteUser(long id) throws Exception;
    User forgotPassword(ForgotPasswordDTO forgotPasswordDTO) throws Exception;

}
