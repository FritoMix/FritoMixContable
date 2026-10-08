package com.fritomix.erp.modules.users.application.service;

import com.fritomix.erp.common.dto.PageResponse;
import com.fritomix.erp.modules.auth.domain.entity.Role;
import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.repository.RoleRepository;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.auth.exception.UserNotFoundException;
import com.fritomix.erp.modules.settings.application.service.SettingService;
import com.fritomix.erp.modules.users.application.dto.request.CreateUserRequest;
import com.fritomix.erp.modules.users.application.dto.request.UpdateUserRequest;
import com.fritomix.erp.modules.users.application.dto.response.UserResponse;
import com.fritomix.erp.modules.users.application.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private com.fritomix.erp.modules.auth.domain.repository.UserRepository userRepository;
    @Mock
    private com.fritomix.erp.modules.auth.domain.repository.RoleRepository roleRepository;
    @Mock
    private com.fritomix.erp.modules.users.application.mapper.UserMapper mapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private com.fritomix.erp.modules.settings.application.service.SettingService settingService;

    @InjectMocks
    private UserService service;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("Ana")
                .lastName("Soto")
                .email("ana@x.com")
                .password("hash")
                .enabled(true)
                .accountNonLocked(true)
                .failedAttempts(3)
                .role(com.fritomix.erp.modules.auth.domain.entity.Role.builder().id(1L).name("ADMIN").build())
                .build();
    }

    private void allowPolicy() {
        when(settingService.getSecurityPolicy())
                .thenReturn(new com.fritomix.erp.modules.settings.application.service.SettingService.SecurityPolicy(8, true, 5));
    }

    @Test
    void findByEmailEncontradoYNoEncontrado() {
        when(userRepository.findByEmail("ana@x.com")).thenReturn(Optional.of(user));
        when(mapper.toResponse(user)).thenReturn(org.mockito.Mockito.mock(com.fritomix.erp.modules.users.application.dto.response.UserResponse.class));

        assertNotNull(service.findByEmail("ana@x.com"));

        when(userRepository.findByEmail("no@x.com")).thenReturn(Optional.empty());
        assertThrows(com.fritomix.erp.modules.auth.exception.UserNotFoundException.class,
                () -> service.findByEmail("no@x.com"));
    }

    @Test
    void updateProfileCambiaCamposYPassword() {
        com.fritomix.erp.modules.users.application.dto.request.UpdateUserRequest request =
                com.fritomix.erp.modules.users.application.dto.request.UpdateUserRequest.builder()
                        .firstName("Bea").lastName("Diaz").password("Abcdef1!").build();
        when(userRepository.findByEmail("ana@x.com")).thenReturn(Optional.of(user));
        allowPolicy();
        when(passwordEncoder.encode("Abcdef1!")).thenReturn("newhash");
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.toResponse(user)).thenReturn(org.mockito.Mockito.mock(com.fritomix.erp.modules.users.application.dto.response.UserResponse.class));

        service.updateProfile("ana@x.com", request);

        assertEquals("Bea", user.getFirstName());
        assertEquals("newhash", user.getPassword());
    }

    @Test
    void updateProfileConEmailDuplicadoLanzaExcepcion() {
        com.fritomix.erp.modules.users.application.dto.request.UpdateUserRequest request =
                com.fritomix.erp.modules.users.application.dto.request.UpdateUserRequest.builder()
                        .email("otro@x.com").build();
        when(userRepository.findByEmail("ana@x.com")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("otro@x.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.updateProfile("ana@x.com", request));
    }

    @Test
    void updateProfileConPasswordCortaLanzaExcepcion() {
        com.fritomix.erp.modules.users.application.dto.request.UpdateUserRequest request =
                com.fritomix.erp.modules.users.application.dto.request.UpdateUserRequest.builder()
                        .password("Ab1!").build();
        when(userRepository.findByEmail("ana@x.com")).thenReturn(Optional.of(user));
        allowPolicy();

        assertThrows(IllegalArgumentException.class, () -> service.updateProfile("ana@x.com", request));
    }

    @Test
    void updateProfileSinCaracterEspecialLanzaExcepcion() {
        com.fritomix.erp.modules.users.application.dto.request.UpdateUserRequest request =
                com.fritomix.erp.modules.users.application.dto.request.UpdateUserRequest.builder()
                        .password("Abcdefgh").build();
        when(userRepository.findByEmail("ana@x.com")).thenReturn(Optional.of(user));
        allowPolicy();

        assertThrows(IllegalArgumentException.class, () -> service.updateProfile("ana@x.com", request));
    }

    @Test
    void findAllMapeaPagina() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.search(null, pageable)).thenReturn(new PageImpl<>(List.of(user), pageable, 1));
        when(mapper.toResponse(user)).thenReturn(org.mockito.Mockito.mock(com.fritomix.erp.modules.users.application.dto.response.UserResponse.class));

        assertEquals(1, service.findAll(null, pageable).content().size());
    }

    @Test
    void findByIdEncontradoYNoEncontrado() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.toResponse(user)).thenReturn(org.mockito.Mockito.mock(com.fritomix.erp.modules.users.application.dto.response.UserResponse.class));

        assertNotNull(service.findById(1L));

        when(userRepository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(com.fritomix.erp.modules.auth.exception.UserNotFoundException.class,
                () -> service.findById(9L));
    }

    @Test
    void createExitoso() {
        com.fritomix.erp.modules.users.application.dto.request.CreateUserRequest request =
                com.fritomix.erp.modules.users.application.dto.request.CreateUserRequest.builder()
                        .firstName("Carlos").lastName("Ruiz").email("c@x.com")
                        .password("Abcdef1!").role("USER").build();
        com.fritomix.erp.modules.auth.domain.entity.Role role =
                com.fritomix.erp.modules.auth.domain.entity.Role.builder().id(2L).name("USER").build();
        when(userRepository.existsByEmail("c@x.com")).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        allowPolicy();
        when(passwordEncoder.encode("Abcdef1!")).thenReturn("enc");
        when(mapper.toEntity(request, role, "enc")).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.toResponse(user)).thenReturn(org.mockito.Mockito.mock(com.fritomix.erp.modules.users.application.dto.response.UserResponse.class));

        assertNotNull(service.create(request));
        verify(userRepository).save(user);
    }

    @Test
    void createConEmailExistenteLanzaExcepcion() {
        com.fritomix.erp.modules.users.application.dto.request.CreateUserRequest request =
                com.fritomix.erp.modules.users.application.dto.request.CreateUserRequest.builder()
                        .email("ana@x.com").role("ADMIN").password("Abcdef1!").build();
        when(userRepository.existsByEmail("ana@x.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.create(request));
    }

    @Test
    void createConRolInexistenteLanzaExcepcion() {
        com.fritomix.erp.modules.users.application.dto.request.CreateUserRequest request =
                com.fritomix.erp.modules.users.application.dto.request.CreateUserRequest.builder()
                        .email("n@x.com").role("FANTASMA").password("Abcdef1!").build();
        when(userRepository.existsByEmail("n@x.com")).thenReturn(false);
        when(roleRepository.findByName("FANTASMA")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.create(request));
    }

    @Test
    void updateCambiaRolYEstado() {
        com.fritomix.erp.modules.users.application.dto.request.UpdateUserRequest request =
                com.fritomix.erp.modules.users.application.dto.request.UpdateUserRequest.builder()
                        .role("VENDEDOR").enabled(false).build();
        com.fritomix.erp.modules.auth.domain.entity.Role role =
                com.fritomix.erp.modules.auth.domain.entity.Role.builder().id(2L).name("VENDEDOR").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("VENDEDOR")).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.toResponse(user)).thenReturn(org.mockito.Mockito.mock(com.fritomix.erp.modules.users.application.dto.response.UserResponse.class));

        service.update(1L, request);

        assertEquals("VENDEDOR", user.getRole().getName());
        assertTrue(!user.getEnabled());
    }

    @Test
    void updateConRolInexistenteLanzaExcepcion() {
        com.fritomix.erp.modules.users.application.dto.request.UpdateUserRequest request =
                com.fritomix.erp.modules.users.application.dto.request.UpdateUserRequest.builder()
                        .role("NADA").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("NADA")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.update(1L, request));
    }

    @Test
    void deleteExitosoYNoEncontrado() {
        when(userRepository.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(userRepository).deleteById(1L);

        when(userRepository.existsById(9L)).thenReturn(false);
        assertThrows(com.fritomix.erp.modules.auth.exception.UserNotFoundException.class,
                () -> service.delete(9L));
    }

    @Test
    void toggleEnabledInvierteEstado() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.toResponse(user)).thenReturn(org.mockito.Mockito.mock(com.fritomix.erp.modules.users.application.dto.response.UserResponse.class));

        service.toggleEnabled(1L);

        assertTrue(!user.getEnabled());
    }

    @Test
    void unlockReseteaBloqueo() {
        user.setAccountNonLocked(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.toResponse(user)).thenReturn(org.mockito.Mockito.mock(com.fritomix.erp.modules.users.application.dto.response.UserResponse.class));

        service.unlock(1L);

        assertTrue(user.getAccountNonLocked());
        assertEquals(0, user.getFailedAttempts());
    }
}