package com.example.pizzashop.controllers;

import com.example.pizzashop.components.LocalizationUtils;
import com.example.pizzashop.dtos.ForgotPasswordDTO;
import com.example.pizzashop.dtos.UpdateUserDTO;
import com.example.pizzashop.dtos.UserDTO;
import com.example.pizzashop.dtos.UserLoginDTO;
import com.example.pizzashop.models.User;
import com.example.pizzashop.responses.*;
import com.example.pizzashop.services.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<RegisterResponse> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result){
        RegisterResponse registerResponse = new RegisterResponse();
        try{
            if(result.hasErrors()){
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                registerResponse.setMessage(errorMessages.toString());
                return ResponseEntity.badRequest().body(registerResponse);
            }
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword())){
                registerResponse.setMessage("Mật khẩu không chính xác");
                return ResponseEntity.badRequest().body(registerResponse);
            }
            User user = userService.createUser(userDTO);
            //return ResponseEntity.ok("Register successfully");
            registerResponse.setMessage("Đăng ký thành công");
            registerResponse.setUser(user);
            return ResponseEntity.ok(registerResponse);
        }catch (Exception e){
            registerResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(registerResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO){
        //kiem tra thoong tin dang nhap va sinh token
        try {
            String token = userService.login(userLoginDTO.getPhone(),
                    userLoginDTO.getPassword()
            );
            return ResponseEntity.ok(LoginResponse.builder()
                    .message("Đăng nhập thành công")
                    .token(token)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(String.format("Đăng nhập không thành công" + e.getMessage()))
                            .build()
            );
        }
        //tra ve token trong response
    }

    @PostMapping("/details")
    public ResponseEntity<UserResponse> getUserDetails(@RequestHeader("Authorization") String token){
        try{
            String extractedToken = token.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/details/{userId}")
    @Transactional
    public ResponseEntity<UserResponse> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO updateUserDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ){
        try{
            String extractedToken = authorizationHeader.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            if(user.getId()!=userId){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if (!updateUserDTO.getPassword().equals(updateUserDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().build();
            }
            User updatedUser = userService.updateUser(userId, updateUserDTO);
            return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("")
    public ResponseEntity<UserListResponse> getAllUsers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ){
        PageRequest pageRequest = PageRequest.of(page,limit, Sort.by("id").ascending());
        Page<UserResponse> userPage = userService.getAllUsers(keyword, pageRequest);
        //lay tong so trang
        int totalPages = userPage.getTotalPages();
        List<UserResponse> users =  userPage.getContent();
        return ResponseEntity.ok(UserListResponse
                .builder()
                .users(users)
                .totalPages(totalPages)
                .build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("Người dùng đã xóa thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{userId}")
    @Transactional
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO updateUserDTO){
        try{
            User updatedUser = userService.updateUser(userId, updateUserDTO);
            return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<UserResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO){
        try{
            User updatedUser = userService.forgotPassword(forgotPasswordDTO);
            return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }


}
