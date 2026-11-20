package com.example.pizzashop.flows.user;

import com.example.pizzashop.components.JwtTokenUtils;
import com.example.pizzashop.components.LocalizationUtils;
import com.example.pizzashop.dtos.ForgotPasswordDTO;
import com.example.pizzashop.dtos.UpdateUserDTO;
import com.example.pizzashop.dtos.UserDTO;
import com.example.pizzashop.exceptions.DataNotFoundException;
import com.example.pizzashop.exceptions.PermissionDenyException;
import com.example.pizzashop.flows.TestData;
import com.example.pizzashop.kafka.KafkaEventPublisher;
import com.example.pizzashop.kafka.KafkaTopics;
import com.example.pizzashop.kafka.events.UserEvent;
import com.example.pizzashop.models.Membership;
import com.example.pizzashop.models.Role;
import com.example.pizzashop.models.User;
import com.example.pizzashop.repositories.MembershipRepository;
import com.example.pizzashop.repositories.RoleRepository;
import com.example.pizzashop.repositories.UserRepository;
import com.example.pizzashop.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Chức năng Người dùng")
class UserFlowTest {
    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock MembershipRepository membershipRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtTokenUtils jwtTokenUtils;
    @Mock AuthenticationManager authenticationManager;
    @Mock LocalizationUtils localizationUtils;
    @Mock KafkaEventPublisher kafkaEventPublisher;

    @InjectMocks UserService userService;

    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = TestData.role(2L, Role.USER);
    }

    private UserEvent capturePublishedEvent() {
        ArgumentCaptor<UserEvent> captor = ArgumentCaptor.forClass(UserEvent.class);
        verify(kafkaEventPublisher).publish(eq(KafkaTopics.USER_EVENTS), any(), captor.capture());
        return captor.getValue();
    }

    @Nested
    @DisplayName("Luồng đăng ký")
    class Register {
        @Test
        @DisplayName("Đăng ký thành công: mã hoá mật khẩu, tạo thẻ thành viên Bronze, gửi USER_REGISTERED")
        void registerSuccess() throws Exception {
            UserDTO dto = TestData.userDTO(2L);
            when(userRepository.existsByPhone(dto.getPhone())).thenReturn(false);
            when(roleRepository.findById(2L)).thenReturn(Optional.of(userRole));
            when(passwordEncoder.encode("123456")).thenReturn("hashed");
            when(membershipRepository.getMembershipByPhone(dto.getPhone())).thenReturn(Optional.empty());

            User user = userService.createUser(dto);

            assertThat(user.getPassword()).isEqualTo("hashed");
            assertThat(user.getRole()).isSameAs(userRole);
            assertThat(user.isActive()).isTrue();
            verify(userRepository).save(user);

            ArgumentCaptor<Membership> membership = ArgumentCaptor.forClass(Membership.class);
            verify(membershipRepository).save(membership.capture());
            assertThat(membership.getValue().getMembershipTier()).isEqualTo(Membership.MEMBER_BRONZE);
            assertThat(membership.getValue().getPhone()).isEqualTo(dto.getPhone());

            UserEvent event = capturePublishedEvent();
            assertThat(event.getEventType()).isEqualTo(UserEvent.REGISTERED);
            assertThat(event.getPhone()).isEqualTo(dto.getPhone());
            assertThat(event.getRole()).isEqualTo(Role.USER);
            assertThat(event.toString()).doesNotContain("hashed").doesNotContain("123456");
        }

        @Test
        @DisplayName("Đã có thẻ thành viên theo số điện thoại: không tạo thẻ mới")
        void registerWithExistingMembership() throws Exception {
            UserDTO dto = TestData.userDTO(2L);
            when(roleRepository.findById(2L)).thenReturn(Optional.of(userRole));
            when(membershipRepository.getMembershipByPhone(dto.getPhone()))
                    .thenReturn(Optional.of(Membership.builder().id(1L).build()));

            userService.createUser(dto);

            verify(membershipRepository, never()).save(any());
        }

        @Test
        @DisplayName("Số điện thoại đã tồn tại: báo lỗi")
        void phoneAlreadyExists() {
            UserDTO dto = TestData.userDTO(2L);
            when(userRepository.existsByPhone(dto.getPhone())).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(dto))
                    .isInstanceOf(DataIntegrityViolationException.class);
            verify(userRepository, never()).save(any());
            verifyNoInteractions(kafkaEventPublisher);
        }

        @Test
        @DisplayName("Không cho phép tự đăng ký tài khoản ADMIN")
        void cannotRegisterAdmin() {
            when(roleRepository.findById(1L)).thenReturn(Optional.of(TestData.role(1L, "admin")));

            assertThatThrownBy(() -> userService.createUser(TestData.userDTO(1L)))
                    .isInstanceOf(PermissionDenyException.class);
            verifyNoInteractions(kafkaEventPublisher);
        }

        @Test
        @DisplayName("Role không tồn tại: báo lỗi")
        void roleNotFound() {
            when(roleRepository.findById(9L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.createUser(TestData.userDTO(9L)))
                    .isInstanceOf(DataNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Luồng đăng nhập")
    class Login {
        @Test
        @DisplayName("Đăng nhập đúng: xác thực và trả về JWT")
        void loginSuccess() throws Exception {
            User user = TestData.user(1L, userRole);
            when(userRepository.findByPhone(TestData.PHONE)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("123456", user.getPassword())).thenReturn(true);
            when(roleRepository.findById(2L)).thenReturn(Optional.of(userRole));
            when(jwtTokenUtils.generateToken(user)).thenReturn("jwt-token");

            String token = userService.login(TestData.PHONE, "123456");

            assertThat(token).isEqualTo("jwt-token");
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }

        @Test
        @DisplayName("Sai số điện thoại: báo lỗi")
        void unknownPhone() {
            when(userRepository.findByPhone("000")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.login("000", "123456"))
                    .isInstanceOf(DataNotFoundException.class);
        }

        @Test
        @DisplayName("Sai mật khẩu: báo lỗi BadCredentials")
        void wrongPassword() {
            User user = TestData.user(1L, userRole);
            when(userRepository.findByPhone(TestData.PHONE)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrong", user.getPassword())).thenReturn(false);

            assertThatThrownBy(() -> userService.login(TestData.PHONE, "wrong"))
                    .isInstanceOf(BadCredentialsException.class);
            verifyNoInteractions(authenticationManager);
        }

        @Test
        @DisplayName("Tài khoản bị khoá: báo lỗi")
        void inactiveAccount() {
            User user = TestData.user(1L, userRole);
            user.setActive(false);
            when(userRepository.findByPhone(TestData.PHONE)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("123456", user.getPassword())).thenReturn(true);
            when(roleRepository.findById(2L)).thenReturn(Optional.of(userRole));

            assertThatThrownBy(() -> userService.login(TestData.PHONE, "123456"))
                    .isInstanceOf(DataNotFoundException.class)
                    .hasMessageContaining("khóa");
            verifyNoInteractions(authenticationManager);
        }
    }

    @Nested
    @DisplayName("Luồng cập nhật / khoá tài khoản / quên mật khẩu")
    class Manage {
        @Test
        @DisplayName("Cập nhật thông tin: lưu và gửi USER_UPDATED")
        void updateUser() throws Exception {
            User user = TestData.user(1L, userRole);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);
            UpdateUserDTO dto = UpdateUserDTO.builder()
                    .fullName("Tên mới").phone(TestData.PHONE).address("Đà Nẵng").roleId(2L).build();

            User updated = userService.updateUser(1L, dto);

            assertThat(updated.getFullName()).isEqualTo("Tên mới");
            assertThat(updated.getAddress()).isEqualTo("Đà Nẵng");
            assertThat(capturePublishedEvent().getEventType()).isEqualTo(UserEvent.UPDATED);
        }

        @Test
        @DisplayName("Đổi sang số điện thoại đã có người dùng: báo lỗi")
        void updateUserPhoneTaken() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(TestData.user(1L, userRole)));
            when(userRepository.existsByPhone("0900000000")).thenReturn(true);
            UpdateUserDTO dto = UpdateUserDTO.builder().phone("0900000000").roleId(2L).build();

            assertThatThrownBy(() -> userService.updateUser(1L, dto))
                    .isInstanceOf(DataIntegrityViolationException.class);
            verifyNoInteractions(kafkaEventPublisher);
        }

        @Test
        @DisplayName("Mật khẩu nhập lại không khớp: báo lỗi")
        void updateUserPasswordMismatch() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(TestData.user(1L, userRole)));
            UpdateUserDTO dto = UpdateUserDTO.builder()
                    .phone(TestData.PHONE).password("a").retypePassword("b").roleId(2L).build();

            assertThatThrownBy(() -> userService.updateUser(1L, dto))
                    .isInstanceOf(DataNotFoundException.class);
        }

        @Test
        @DisplayName("Khoá tài khoản: xoá mềm và gửi USER_DEACTIVATED")
        void deactivateUser() throws Exception {
            User user = TestData.user(1L, userRole);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            userService.deleteUser(1L);

            assertThat(user.isActive()).isFalse();
            verify(userRepository).save(user);
            assertThat(capturePublishedEvent().getEventType()).isEqualTo(UserEvent.DEACTIVATED);
        }

        @Test
        @DisplayName("Quên mật khẩu đúng email: đặt mật khẩu mới và gửi USER_PASSWORD_RESET")
        void forgotPassword() throws Exception {
            User user = TestData.user(1L, userRole);
            when(userRepository.findByPhone(TestData.PHONE)).thenReturn(Optional.of(user));
            when(passwordEncoder.encode("newpass")).thenReturn("new-hash");
            when(userRepository.save(user)).thenReturn(user);
            ForgotPasswordDTO dto = ForgotPasswordDTO.builder()
                    .phone(TestData.PHONE).email(user.getEmail())
                    .password("newpass").retypePassword("newpass").build();

            User saved = userService.forgotPassword(dto);

            assertThat(saved.getPassword()).isEqualTo("new-hash");
            assertThat(capturePublishedEvent().getEventType()).isEqualTo(UserEvent.PASSWORD_RESET);
        }

        @Test
        @DisplayName("Quên mật khẩu sai email: báo lỗi")
        void forgotPasswordWrongEmail() {
            when(userRepository.findByPhone(TestData.PHONE)).thenReturn(Optional.of(TestData.user(1L, userRole)));
            ForgotPasswordDTO dto = ForgotPasswordDTO.builder()
                    .phone(TestData.PHONE).email("other@example.com")
                    .password("x").retypePassword("x").build();

            assertThatThrownBy(() -> userService.forgotPassword(dto))
                    .isInstanceOf(DataNotFoundException.class);
            verify(userRepository, never()).save(any());
            verifyNoInteractions(kafkaEventPublisher);
        }
    }
}
