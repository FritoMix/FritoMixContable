package com.fritomix.erp.modules.users.application.service;

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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final SettingService settingService;

    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^A-Za-z0-9]");

    private void validatePasswordPolicy(String password) {
        SettingService.SecurityPolicy policy = settingService.getSecurityPolicy();
        if (StringUtils.hasText(password) && password.length() < policy.passwordMinLength()) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos " + policy.passwordMinLength() + " caracteres");
        }
        if (policy.passwordRequireSpecial()
                && (!StringUtils.hasText(password) || !SPECIAL_CHAR_PATTERN.matcher(password).find())) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un carácter especial");
        }
    }

    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + email));
        return mapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(String email, UpdateUserRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + email));

        if (request.firstName() != null) user.setFirstName(request.firstName());
        if (request.lastName() != null) user.setLastName(request.lastName());
        if (request.email() != null) {
            if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
                throw new IllegalArgumentException("El email ya está registrado: " + request.email());
            }
            user.setEmail(request.email());
        }
        if (request.password() != null) {
            validatePasswordPolicy(request.password());
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        user = userRepository.save(user);
        return mapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + id));
        return mapper.toResponse(user);
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email ya está registrado: " + request.email());
        }

        Role role = roleRepository.findByName(request.role())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + request.role()));

        validatePasswordPolicy(request.password());
        String encodedPassword = passwordEncoder.encode(request.password());
        User user = mapper.toEntity(request, role, encodedPassword);
        user = userRepository.save(user);
        return mapper.toResponse(user);
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + id));

        if (request.firstName() != null) user.setFirstName(request.firstName());
        if (request.lastName() != null) user.setLastName(request.lastName());
        if (request.email() != null) {
            if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
                throw new IllegalArgumentException("El email ya está registrado: " + request.email());
            }
            user.setEmail(request.email());
        }
        if (request.password() != null) {
            validatePasswordPolicy(request.password());
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        if (request.role() != null) {
            Role role = roleRepository.findByName(request.role())
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + request.role()));
            user.setRole(role);
        }
        if (request.enabled() != null) {
            user.setEnabled(request.enabled());
        }

        user = userRepository.save(user);
        return mapper.toResponse(user);
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Usuario no encontrado con id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponse toggleEnabled(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + id));
        user.setEnabled(!user.getEnabled());
        user = userRepository.save(user);
        return mapper.toResponse(user);
    }

    @Transactional
    public UserResponse unlock(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con id: " + id));
        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);
        user = userRepository.save(user);
        return mapper.toResponse(user);
    }
}
