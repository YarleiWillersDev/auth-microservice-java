package br.com.confidence.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.confidence.dto.user.UserRequest;
import br.com.confidence.dto.user.UserResponse;
import br.com.confidence.model.role.Role;
import br.com.confidence.model.user.User;
import br.com.confidence.repository.role.RoleRepository;
import br.com.confidence.repository.user.UserRepository;
import br.com.confidence.updater.user.UserUpdater;
import br.com.confidence.validation.user.UserValidation;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserValidation userValidation;

    @Mock
    private UserUpdater userUpdater;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void setUp() {
        userServiceImpl = new UserServiceImpl(userRepository, roleRepository, userValidation, userUpdater, passwordEncoder);
    }

    @Nested
    class CreateTest {

        @Test
        void shouldCreateUserWhenReceivedDataIsCorrect() {

            Role defaultRole = new Role();
            defaultRole.setId(1L);
            defaultRole.setName("ROLE_USER");
            defaultRole.setDescription("Default role");

            UserRequest user = new UserRequest("Yarlei", "teste@email.com", "SenhaForte@123");

            User savedUser = new User();
            savedUser.setId(1L);
            savedUser.setName("Yarlei");
            savedUser.setEmail(user.email());
            savedUser.setPassword("encoded-password");
            savedUser.setRoles(List.of(defaultRole));

            when(userRepository.existsByEmail(user.email())).thenReturn(false);
            when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(defaultRole));
            when(passwordEncoder.encode(user.password())).thenReturn("encoded-password");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            UserResponse response = userServiceImpl.create(user);

            verify(userValidation).validateUserRequest(user);
            verify(userRepository).existsByEmail(user.email());
            verify(roleRepository).findByName("ROLE_USER");
            verify(passwordEncoder).encode(user.password());
            verify(userRepository).save(any(User.class));

            assertEquals(savedUser.getId(), response.id());
            assertEquals(savedUser.getName(), response.name());
            assertEquals(savedUser.getEmail(),response.email());
        }

    }
}
