package br.com.confidence.mapper.user;

import java.util.List;

import br.com.confidence.dto.user.UserRequest;
import br.com.confidence.dto.user.UserResponse;
import br.com.confidence.mapper.role.RoleMapper;
import br.com.confidence.model.user.User;

public final class UserMapper {

    private UserMapper() {}

    public static User toEntity(UserRequest userRequest) {
        User user = new User();
        user.setName(userRequest.name());
        user.setEmail(userRequest.email());
        user.setPassword(userRequest.password());
        return user;
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            RoleMapper.toResponse(user.getRoles())
        );
    }

    public static List<UserResponse> toResponse(List<User> users) {
        return users.stream().map(UserMapper::toResponse).toList();
    }

}
