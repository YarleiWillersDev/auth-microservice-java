package br.com.confidence.service.user;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.confidence.dto.user.UserEmailUpdateRequest;
import br.com.confidence.dto.user.UserPasswordUpdateRequest;
import br.com.confidence.dto.user.UserRequest;
import br.com.confidence.dto.user.UserResponse;
import br.com.confidence.dto.user.UserUpdateRequest;
import br.com.confidence.exception.role.RoleNotFoundException;
import br.com.confidence.exception.user.CurrentPasswordIncorrectException;
import br.com.confidence.exception.user.UserAlreadyExistsException;
import br.com.confidence.exception.user.UserNotFoundException;
import br.com.confidence.mapper.user.UserMapper;
import br.com.confidence.model.role.Role;
import br.com.confidence.model.user.User;
import br.com.confidence.repository.role.RoleRepository;
import br.com.confidence.repository.user.UserRepository;
import br.com.confidence.updater.user.UserUpdater;
import br.com.confidence.validation.user.UserValidation;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserValidation userValidation;
    private final UserUpdater userUpdater;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
            UserValidation userValidation, UserUpdater userUpdater, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userValidation = userValidation;
        this.userUpdater = userUpdater;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse create(UserRequest userRequest) {
        userValidation.validateUserRequest(userRequest);

        if (userRepository.existsByEmail(userRequest.email())) {
            throw new UserAlreadyExistsException("User already exists with this email");
        }

        List<Role> roles = roleRepository.findByName("USER");
        Role defaultRole = roles.stream()
                .findFirst()
                .orElseThrow(() -> new RoleNotFoundException("Default role not found"));

        User user = UserMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        user.setRoles(List.of(defaultRole));

        User savedUser = userRepository.save(user);

        return UserMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse update(UserUpdateRequest userRequest, long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String newName = userRequest.name();
        userValidation.validateNameUserRequest(newName);

        userUpdater.updateUsername(user, newName);

        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse updateEmail(UserEmailUpdateRequest userRequest, long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String newEmail = userRequest.email();
        userValidation.validateEmailUserRequest(newEmail);
        validateEmailUniquenessForUpdate(user, newEmail);

        userUpdater.updateEmail(user, newEmail);

        User savedUser = userRepository.save(user);

        return UserMapper.toResponse(savedUser);
    }

    private void validateEmailUniquenessForUpdate(User user, String newEmail) {
        if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
            throw new UserAlreadyExistsException("User already exists with this email");
        }
    }

    @Override
    @Transactional
    public UserResponse updatePassword(UserPasswordUpdateRequest userRequest, long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        validateCurrentPasswordPassedByUser(user, userRequest.currentPassword());
        userValidation.validatePasswordUserRequest(userRequest.newPassword());

        String encodePassword = passwordEncoder.encode(userRequest.newPassword());
        userUpdater.updatePassword(user, encodePassword);

        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }

    private void validateCurrentPasswordPassedByUser(User user, String currentPassword) {
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new CurrentPasswordIncorrectException("Current password is incorrect");
        }
    }

    @Override
    @Transactional
    public void delete(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse searchByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with this email"));

        return UserMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> searchByName(String name) {
        List<User> users = userRepository.findByNameContainingIgnoreCase(name);

        return UserMapper.toResponse(users);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> listAll() {
        List<User> users = userRepository.findAll();
        return UserMapper.toResponse(users);
    }

}
