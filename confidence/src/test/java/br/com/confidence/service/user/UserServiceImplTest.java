package br.com.confidence.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

import br.com.confidence.dto.user.UserEmailUpdateRequest;
import br.com.confidence.dto.user.UserPasswordUpdateRequest;
import br.com.confidence.dto.user.UserRequest;
import br.com.confidence.dto.user.UserResponse;
import br.com.confidence.dto.user.UserUpdateRequest;
import br.com.confidence.exception.role.RoleNotFoundException;
import br.com.confidence.exception.user.CurrentPasswordIncorrectException;
import br.com.confidence.exception.user.InvalidUserEmailException;
import br.com.confidence.exception.user.InvalidUserPasswordException;
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

    @Nested
    class UpdateEmail {

        @Test
        void shouldUpdateUserEmailWhenDataIsValid() {
            long id = 1L;
            UserEmailUpdateRequest request = new UserEmailUpdateRequest("new@email.com");

            User existingUser = new User();
            existingUser.setId(id);
            existingUser.setName("User");
            existingUser.setEmail("old@email.com");
            existingUser.setRoles(List.of());

            when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(userRepository.save(existingUser)).thenReturn(existingUser);

            doAnswer(invocation -> {
                User userArg = invocation.getArgument(0, User.class);
                String newEmailArg = invocation.getArgument(1, String.class);
                userArg.setEmail(newEmailArg);
                return null;
            }).when(userUpdater).updateEmail(existingUser, "new@email.com");

            UserResponse response = userServiceImpl.updateEmail(request, id);

            verify(userRepository).findById(id);
            verify(userValidation).validateEmailUserRequest(request.email());
            verify(userRepository).existsByEmail(request.email());
            verify(userUpdater).updateEmail(existingUser, request.email());
            verify(userRepository).save(existingUser);

            assertEquals(request.email(), response.email());
        }

        @Test
        void shouldThrowUserAlreadyExistsExceptionWhenUpdatingWithExistingEmail() {
            long id = 1L;
            String newEmail = "existing@email.com";
            UserEmailUpdateRequest request = new UserEmailUpdateRequest(newEmail);

            User existingUser = new User();
            existingUser.setId(id);
            existingUser.setName("User");
            existingUser.setEmail("old@rmail.com");
            existingUser.setRoles(List.of());

            when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail(newEmail)).thenReturn(true);

            assertThrows(UserAlreadyExistsException.class, () -> userServiceImpl.updateEmail(request, id));

            verify(userRepository).findById(id);
            verify(userValidation).validateEmailUserRequest(newEmail);
            verify(userRepository).existsByEmail(newEmail);
            verifyNoInteractions(userUpdater);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void shouldThrowInvalidUserEmailExceptionWhenEmailIsInvalidOnUpdate() {
            long id = 1L;
            String invalidEmail = "inv";
            UserEmailUpdateRequest request = new UserEmailUpdateRequest(invalidEmail);

            User existingUser = new User();
            existingUser.setId(id);
            existingUser.setName("User");
            existingUser.setEmail("old@email.com");
            existingUser.setRoles(List.of());

            when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));

            doThrow(new InvalidUserEmailException(
                    "Invalid user email address. The email address must contain at least 15 characters."))
                    .when(userValidation).validateEmailUserRequest(invalidEmail);

            assertThrows(InvalidUserEmailException.class, () -> userServiceImpl.updateEmail(request, id));

            verify(userRepository).findById(id);
            verify(userValidation).validateEmailUserRequest(invalidEmail);
            verify(userRepository, never()).existsByEmail(anyString());
            verifyNoInteractions(userUpdater);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenIdDoesNotExistOnUpdate() {
            long id = 999L;
            UserEmailUpdateRequest request = new UserEmailUpdateRequest("new@email.com");

            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> userServiceImpl.updateEmail(request, id));

            verify(userRepository).findById(id);
            verifyNoInteractions(userValidation, userUpdater);
            verify(userRepository, never()).existsByEmail(anyString());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    class updatePassword {

        @Test
        void shouldUpdatePasswordWhenDataIsValid() {
            long id = 1L;
            UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("Current@123", "NewStrong@123");

            User user = new User();
            user.setId(id);
            user.setName("User");
            user.setEmail("user@email.com");
            user.setPassword("encoded-current");
            user.setRoles(List.of());

            when(userRepository.findById(id)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("Current@123", "encoded-current")).thenReturn(true);
            when(passwordEncoder.encode("NewStrong@123")).thenReturn("encoded-new");
            when(userRepository.save(user)).thenReturn(user);

            UserResponse response = userServiceImpl.updatePassword(request, id);

            verify(userRepository).findById(id);
            verify(passwordEncoder).matches("Current@123", "encoded-current");
            verify(userValidation).validatePasswordUserRequest("NewStrong@123");
            verify(passwordEncoder).encode("NewStrong@123");
            verify(userUpdater).updatePassword(user, "encoded-new");
            verify(userRepository).save(user);

            assertEquals(id, response.id());
        }

        @Test
        void shouldThrowCurrentPasswordIncorrectExceptionWhenCurrentPasswordDoesNotMatch() {
            long id = 1L;
            UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("WrongPassword@123", "NewStrong@123");

            User user = new User();
            user.setId(id);
            user.setPassword("encoded-current");
            user.setRoles(List.of());

            when(userRepository.findById(id)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("WrongPassword@123", "encoded-current")).thenReturn(false);

            assertThrows(CurrentPasswordIncorrectException.class,
                    () -> userServiceImpl.updatePassword(request, id));

            verify(userRepository).findById(id);
            verify(passwordEncoder).matches("WrongPassword@123", "encoded-current");
            verifyNoInteractions(userUpdater);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void shouldThrowInvalidUserPasswordExceptionWhenNewPasswordIsInvalid() {
            long id = 1L;
            UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("Current@123", "weak"); // senha fraca

            User user = new User();
            user.setId(id);
            user.setPassword("encoded-current");
            user.setRoles(List.of());

            when(userRepository.findById(id)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("Current@123", "encoded-current")).thenReturn(true);

            doThrow(new InvalidUserPasswordException("Password must have at least 8 characters"))
                    .when(userValidation).validatePasswordUserRequest("weak");

            assertThrows(InvalidUserPasswordException.class,
                    () -> userServiceImpl.updatePassword(request, id));

            verify(userRepository).findById(id);
            verify(passwordEncoder).matches("Current@123", "encoded-current");
            verify(userValidation).validatePasswordUserRequest("weak");
            verifyNoInteractions(userUpdater);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenIdDoesNotExistOnPasswordUpdate() {
            long id = 999L;
            UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("Current@123", "NewStrong@123");

            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> userServiceImpl.updatePassword(request, id));

            verify(userRepository).findById(id);
            verifyNoInteractions(passwordEncoder, userValidation, userUpdater);
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    class DeleteUser {

        @Test
        void shouldDeleteUserWhenIdExists() {
            long id = 1L;

            User user = new User();
            user.setId(id);
            user.setName("User");
            user.setRoles(List.of());

            when(userRepository.findById(id)).thenReturn(Optional.of(user));

            userServiceImpl.delete(id);

            verify(userRepository).findById(id);
            verify(userRepository).delete(user);
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenDeletingNonExistingUser() {
            long id = 999L;

            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> userServiceImpl.delete(id));

            verify(userRepository).findById(id);
            verify(userRepository, never()).delete(any(User.class));
        }
    }

    @Nested
    class SerchaById {
        @Test
        void shouldReturnUserResponseWhenEmailExists() {
            String email = "user@email.com";

            User user = new User();
            user.setId(1L);
            user.setName("User");
            user.setEmail(email);
            user.setRoles(List.of());

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

            UserResponse response = userServiceImpl.searchByEmail(email);

            verify(userRepository).findByEmail(email);
            assertEquals(user.getId(), response.id());
            assertEquals(user.getName(), response.name());
            assertEquals(user.getEmail(), response.email());
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenEmailDoesNotExist() {
            String email = "notfound@email.com";

            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> userServiceImpl.searchByEmail(email));

            verify(userRepository).findByEmail(email);
        }
    }

    @Nested
    class SearchByNameTest {

        @Test
        void shouldReturnUserListWhenNameMatches() {
            String name = "yar";

            User user1 = new User();
            user1.setId(1L);
            user1.setName("Yarlei");
            user1.setEmail("yarlei1@email.com");
            user1.setRoles(List.of());

            User user2 = new User();
            user2.setId(2L);
            user2.setName("Yara");
            user2.setEmail("yara@email.com");
            user2.setRoles(List.of());

            List<User> users = List.of(user1, user2);

            when(userRepository.findByNameContainingIgnoreCase(name)).thenReturn(users);

            List<UserResponse> responses = userServiceImpl.searchByName(name);

            verify(userRepository).findByNameContainingIgnoreCase(name);
            assertEquals(2, responses.size());
            assertEquals("Yarlei", responses.get(0).name());
            assertEquals("Yara", responses.get(1).name());
        }

        @Test
        void shouldReturnEmptyListWhenNoUserMatchesName() {
            String name = "does-not-exist";

            when(userRepository.findByNameContainingIgnoreCase(name))
                    .thenReturn(List.of());

            List<UserResponse> responses = userServiceImpl.searchByName(name);

            verify(userRepository).findByNameContainingIgnoreCase(name);
            assertTrue(responses.isEmpty());
        }
    }

    @Nested
    class ListAllTest {

        @Test
        void shouldReturnAllUsersWhenListIsNotEmpty() {
            User user1 = new User();
            user1.setId(1L);
            user1.setName("User One");
            user1.setEmail("one@email.com");
            user1.setRoles(List.of());

            User user2 = new User();
            user2.setId(2L);
            user2.setName("User Two");
            user2.setEmail("two@email.com");
            user2.setRoles(List.of());

            List<User> users = List.of(user1, user2);

            when(userRepository.findAll()).thenReturn(users);

            List<UserResponse> responses = userServiceImpl.listAll();

            verify(userRepository).findAll();
            assertEquals(2, responses.size());
            assertEquals("User One", responses.get(0).name());
            assertEquals("User Two", responses.get(1).name());
        }

        @Test
        void shouldReturnEmptyListWhenNoUsersExist() {
            when(userRepository.findAll()).thenReturn(List.of());

            List<UserResponse> responses = userServiceImpl.listAll();

            verify(userRepository).findAll();
            assertTrue(responses.isEmpty());
        }
    }

}
