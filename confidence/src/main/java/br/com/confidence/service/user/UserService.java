package br.com.confidence.service.user;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.confidence.dto.user.UserEmailUpdateRequest;
import br.com.confidence.dto.user.UserPasswordUpdateRequest;
import br.com.confidence.dto.user.UserRequest;
import br.com.confidence.dto.user.UserResponse;
import br.com.confidence.dto.user.UserUpdateRequest;

@Service
public interface UserService {

    public UserResponse create(UserRequest userRequest);
    public UserResponse update(UserUpdateRequest userRequest, long id);
    public UserResponse updateEmail(UserEmailUpdateRequest userRequest, long id);
    public UserResponse updatePassword(UserPasswordUpdateRequest userRequest, long id);
    public void delete(long id);
    public UserResponse searchByEmail(String email);
    public List<UserResponse> searchByName(String name);
    public List<UserResponse> listAll();

}
