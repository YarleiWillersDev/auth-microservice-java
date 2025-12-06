package br.com.confidence.service.user;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.confidence.dto.user.UserEmailUpdateRequest;
import br.com.confidence.dto.user.UserPasswordUpdateRequest;
import br.com.confidence.dto.user.UserRequest;
import br.com.confidence.dto.user.UserResponse;
import br.com.confidence.dto.user.UserUpdateRequest;
import br.com.confidence.exception.role.RoleNotFoundException;
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
    public UserResponse create(UserRequest userRequest)  {
        userValidation.validateUserRequest(userRequest);

        if (userRepository.existsByEmail(userRequest.email())) {
            throw new UserAlreadyExistsException("User already exists with this email");
        }

        Role defaultRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RoleNotFoundException("Default role not found"));

        User user = UserMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        user.setRoles(List.of(defaultRole));

        User savedUser = userRepository.save(user);

        return UserMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse update(UserUpdateRequest userRequest, long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        userRequest.name().ifPresent(userValidation::validateNameUserRequest);
        userUpdater.updateUsername(user, userRequest);

        User savedUser = userRepository.save(user);

        return UserMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse updateEmail(UserEmailUpdateRequest userRequest, long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateEmail'");
    }

    @Override
    public UserResponse updatePassword(UserPasswordUpdateRequest userRequest, long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatePassword'");
    }

    @Override
    public void delete(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public UserResponse searchByEmail(String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByEmail'");
    }

    @Override
    public List<UserResponse> searchByName(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchByName'");
    }

    @Override
    public List<UserResponse> listAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listAll'");
    }

}
