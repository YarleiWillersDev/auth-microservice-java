package br.com.confidence.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import br.com.confidence.dto.user.UserUpdateRequest;
import br.com.confidence.exception.role.RoleNotFoundException;
import br.com.confidence.exception.user.InvalidUsernameException;
import br.com.confidence.exception.user.UserAlreadyExistsException;
import br.com.confidence.exception.user.UserNotFoundException;
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
        userServiceImpl = new UserServiceImpl(userRepository, roleRepository, userValidation, userUpdater,
                passwordEncoder);
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
            assertEquals(savedUser.getEmail(), response.email());
        }

        @Test
        void shouldReturnErrorWhenTryingCreateUserWithEmailAlreadyRegisteredInDatabase() {

            UserRequest user = new UserRequest("Yarlei", "teste@email.com", "SenhaForte@123");

            when(userRepository.existsByEmail(user.email())).thenReturn(true);

            assertThrows(UserAlreadyExistsException.class, () -> userServiceImpl.create(user));

            verify(userValidation).validateUserRequest(user);
            verify(userRepository).existsByEmail(user.email());
            verifyNoInteractions(roleRepository, userUpdater, passwordEncoder);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void shouldReturnErrorWhenTryingCreateUserWithRoleThatIsNotFound() {

            UserRequest request = new UserRequest("Yarlei", "teste@email.com", "SenhaForte@123");

            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

            assertThrows(RoleNotFoundException.class, () -> userServiceImpl.create(request));

            verify(userValidation).validateUserRequest(request);
            verify(userRepository).existsByEmail(request.email());
            verify(roleRepository).findByName("ROLE_USER");
            verifyNoInteractions(passwordEncoder, userUpdater);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void shouldThrowExceptionWhenUserValidationFailsOnCreate() {
            UserRequest request = new UserRequest(
                    "Y",
                    "email-invalido",
                    "fraca");

            doThrow(new InvalidUsernameException("Username cannot be null/empty or contain fewer than 3 letters."))
                    .when(userValidation).validateUserRequest(request);

            assertThrows(InvalidUsernameException.class,
                    () -> userServiceImpl.create(request));

            verify(userValidation).validateUserRequest(request);
            verifyNoInteractions(roleRepository, passwordEncoder, userUpdater);
            verify(userRepository, never()).existsByEmail(any());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    class UpdateTest {

        @Test
        void shouldUpdateUserNameWhenDataIsValid() {
            long id = 1L;
            UserUpdateRequest request = new UserUpdateRequest("Novo Nome");

            User existingUser = new User();
            existingUser.setId(id);
            existingUser.setName("Antigo Nome");
            existingUser.setRoles(List.of());

            when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
            when(userRepository.save(existingUser)).thenReturn(existingUser);

            doAnswer(invocation -> {
                User userArg = invocation.getArgument(0, User.class);
                String newNameArg = invocation.getArgument(1, String.class);
                userArg.setName(newNameArg);
                return null;
            }).when(userUpdater).updateUsername(existingUser, "Novo Nome");

            UserResponse response = userServiceImpl.update(request, id);

            verify(userRepository).findById(id);
            verify(userValidation).validateNameUserRequest("Novo Nome");
            verify(userUpdater).updateUsername(existingUser, "Novo Nome");
            verify(userRepository).save(existingUser);

            assertEquals("Novo Nome", response.name());
        }

        @Test
        void shouldThrowInvalidUsernameExceptionWhenNameIsInvalidOnUpdate() {
            long id = 1L;
            UserUpdateRequest request = new UserUpdateRequest("Jo");

            User existingUser = new User();
            existingUser.setId(id);
            existingUser.setName("Antigo Nome");
            existingUser.setRoles(List.of());

            when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));

            doThrow(new InvalidUsernameException("Username cannot be null/empty or contain fewer than 3 letters."))
                    .when(userValidation).validateNameUserRequest("Jo");

            assertThrows(InvalidUsernameException.class,
                    () -> userServiceImpl.update(request, id));

            verify(userRepository).findById(id);
            verify(userValidation).validateNameUserRequest("Jo");
            verifyNoInteractions(userUpdater);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void shouldThrowInvalidUsernameExceptionWhenNameIsNullOnUpdate() {
            long id = 1L;
            UserUpdateRequest request = new UserUpdateRequest(null);

            User existingUser = new User();
            existingUser.setId(id);
            existingUser.setName("Antigo Nome");
            existingUser.setRoles(List.of()); 

            when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));

            doThrow(new InvalidUsernameException("Username cannot be null/empty or contain fewer than 3 letters."))
                    .when(userValidation).validateNameUserRequest(null);

            assertThrows(InvalidUsernameException.class,
                    () -> userServiceImpl.update(request, id));

            verify(userRepository).findById(id);
            verify(userValidation).validateNameUserRequest(null);
            verifyNoInteractions(userUpdater);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenIdDoesNotExistOnUpdate() {
            long id = 999L;
            UserUpdateRequest request = new UserUpdateRequest("Novo Nome");

            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> userServiceImpl.update(request, id));

            verify(userRepository).findById(id);
            verifyNoInteractions(userValidation, userUpdater);
            verify(userRepository, never()).save(any(User.class));
        }

    }
}
