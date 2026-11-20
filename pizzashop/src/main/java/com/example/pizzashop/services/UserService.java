package com.example.pizzashop.services;

import com.example.pizzashop.components.JwtTokenUtils;
import com.example.pizzashop.components.LocalizationUtils;
import com.example.pizzashop.dtos.ForgotPasswordDTO;
import com.example.pizzashop.dtos.UpdateUserDTO;
import com.example.pizzashop.dtos.UserDTO;
import com.example.pizzashop.exceptions.DataNotFoundException;
import com.example.pizzashop.kafka.KafkaEventPublisher;
import com.example.pizzashop.kafka.KafkaTopics;
import com.example.pizzashop.kafka.events.UserEvent;
import com.example.pizzashop.exceptions.PermissionDenyException;
import com.example.pizzashop.models.Membership;
import com.example.pizzashop.models.Pizza;
import com.example.pizzashop.models.Role;
import com.example.pizzashop.models.User;
import com.example.pizzashop.repositories.MembershipRepository;
import com.example.pizzashop.repositories.RoleRepository;
import com.example.pizzashop.repositories.UserRepository;
import com.example.pizzashop.responses.PizzaResponse;
import com.example.pizzashop.responses.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MembershipRepository membershipRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private  final LocalizationUtils localizationUtils;
    private final KafkaEventPublisher kafkaEventPublisher;
    @Override
    @Transactional
    public User createUser(UserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhone();
        //kiem tra sdt da ton tai hay chua
        if(userRepository.existsByPhone(phoneNumber)){
            throw  new DataIntegrityViolationException("Phone number already exists");

            //convert from userDTo -> user
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(()-> new DataNotFoundException("Role not found"));
        if(role.getRoleName().toUpperCase().equals(Role.ADMIN)){
            throw new PermissionDenyException("You cannot register an admin account");
        }
        User newUser = User.builder()
                .username(userDTO.getUsername())
                .fullName(userDTO.getFullName())
                .email(userDTO.getEmail())
                .phone(userDTO.getPhone())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .active(true)
                .build();

        newUser.setRole(role);
        String password = userDTO.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        newUser.setPassword(encodedPassword);
        userRepository.save(newUser);
        Membership existingMembership = membershipRepository.getMembershipByPhone(userDTO.getPhone()).orElse(null);
        if(existingMembership == null){
            Membership newMembership = Membership.builder()
                    .phone(newUser.getPhone())
                    .totalSpent(Float.valueOf(0))
                    .membershipTier(Membership.MEMBER_BRONZE)
                    .discountRate(Float.valueOf(0))
                    .build();
            membershipRepository.save(newMembership);
        }
        kafkaEventPublisher.publish(KafkaTopics.USER_EVENTS, newUser.getId(),
                UserEvent.of(UserEvent.REGISTERED, newUser));
        return newUser;
    }

    @Override
    public String login(String phoneNumber, String password) throws Exception {
        Optional<User> optionalUser= userRepository.findByPhone(phoneNumber);
        if(optionalUser.isEmpty()){
            throw new DataNotFoundException(
                    "Số điện thoại hoặc mật khẩu ko chính xác");
        }

        //return optionalUser.get();//muon tra ve JWT token
        User existingUser = optionalUser.get();
        if(!passwordEncoder.matches(password,existingUser.getPassword())){
            throw  new BadCredentialsException(
                    "Mật khẩu không chính xác");
        }
        Long roleId = existingUser.getRole().getId();
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if(optionalRole.isEmpty()||!roleId.equals(existingUser.getRole().getId())){
            throw new DataNotFoundException(
                    "Role không tồn tại");
        }
        if(!optionalUser.get().isActive()){
            throw new DataNotFoundException(
                    "Tài khoản của bạn đã bị khóa");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(phoneNumber,password,
                        existingUser.getAuthorities());
        //Authenticate with Java Spring Security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtil.isTokenExpired(token)){
            throw new Exception("Token is expried");
        }
        String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
        Optional<User> user =userRepository.findByPhone(phoneNumber);

        if(user.isPresent()){
            return  user.get();

        }else {
            throw new Exception("User not found");
        }
    }

    @Override
    public User updateUser(Long userId, UpdateUserDTO updateUserDTO) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(()->new DataNotFoundException("User not found"));
        String newPhoneNumber = updateUserDTO.getPhone();
        if(!existingUser.getPhone().equals(newPhoneNumber) &&
                userRepository.existsByPhone(newPhoneNumber)){
            throw  new DataIntegrityViolationException("Phone number already exist");
        }

        if(updateUserDTO.getFullName()!=null){
            existingUser.setFullName(updateUserDTO.getFullName());
        }
        if (newPhoneNumber != null) {
            existingUser.setPhone(newPhoneNumber);
        }
        if (updateUserDTO.getAddress() != null) {
            existingUser.setAddress(updateUserDTO.getAddress());
        }
        if (updateUserDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updateUserDTO.getDateOfBirth());
        }
        if (updateUserDTO.getEmail() != null) {
            existingUser.setEmail(updateUserDTO.getEmail());
        }



        if(updateUserDTO.getPassword()!=null
                && !updateUserDTO.getPassword().isEmpty()){
            if(!updateUserDTO.getPassword().equals(updateUserDTO.getRetypePassword())){
                throw new DataNotFoundException("Password and retype password not the same");
            }
            String newPassword = updateUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }

        if(existingUser.getRole().getId() != updateUserDTO.getRoleId()){
            Role role = roleRepository.findById(updateUserDTO.getRoleId()).orElse(null);
            existingUser.setRole(role);
        }

        User savedUser = userRepository.save(existingUser);
        kafkaEventPublisher.publish(KafkaTopics.USER_EVENTS, savedUser.getId(),
                UserEvent.of(UserEvent.UPDATED, savedUser));
        return savedUser;
    }

    @Override
    public Page<UserResponse> getAllUsers(String keyword, PageRequest pageRequest) {
        Page<User> usersPage;
        usersPage = userRepository.getAllUsers(keyword, pageRequest);
        return usersPage.map(UserResponse::fromUser);
    }

    @Override
    public void deleteUser(long id) throws Exception {
        User existingUser = userRepository.findById(id).orElse(null);
        if(existingUser!=null){
            existingUser.setActive(false);
            userRepository.save(existingUser);
            kafkaEventPublisher.publish(KafkaTopics.USER_EVENTS, existingUser.getId(),
                    UserEvent.of(UserEvent.DEACTIVATED, existingUser));
        }
    }

    @Override
    public User forgotPassword(ForgotPasswordDTO forgotPasswordDTO) throws Exception {
        Optional<User> optionalUser= userRepository.findByPhone(forgotPasswordDTO.getPhone());
        if(optionalUser.isEmpty()){
            throw new DataNotFoundException(
                    "Số điện thoại hoặc mật khẩu ko chính xác");
        }

        User existingUser = optionalUser.get();
        if(!existingUser.getEmail().equals(forgotPasswordDTO.getEmail()) ){
            throw new DataNotFoundException(
                    "Email ko chính xác");
        }
        if(existingUser.getPassword()!=null
                && !forgotPasswordDTO.getPassword().isEmpty()){
            if(!forgotPasswordDTO.getPassword().equals(forgotPasswordDTO.getRetypePassword())){
                throw new DataNotFoundException("Password and retype password not the same");
            }
            String newPassword = forgotPasswordDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }
        User savedUser = userRepository.save(existingUser);
        kafkaEventPublisher.publish(KafkaTopics.USER_EVENTS, savedUser.getId(),
                UserEvent.of(UserEvent.PASSWORD_RESET, savedUser));
        return savedUser;
    }
}
