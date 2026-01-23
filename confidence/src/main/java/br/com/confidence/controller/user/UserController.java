package br.com.confidence.controller.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.confidence.dto.user.UserEmailUpdateRequest;
import br.com.confidence.dto.user.UserPasswordUpdateRequest;
import br.com.confidence.dto.user.UserRequest;
import br.com.confidence.dto.user.UserResponse;
import br.com.confidence.dto.user.UserUpdateRequest;
import br.com.confidence.service.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@Validated
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse user = userService.create(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable long id, @Valid @RequestBody UserUpdateRequest updateRequest) {
        UserResponse user = userService.update(updateRequest, id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/email/{id}")
    public ResponseEntity<UserResponse> updateUserEmail(@PathVariable long id, @Valid @RequestBody UserEmailUpdateRequest userEmailUpdateRequest) {
        UserResponse user = userService.updateEmail(userEmailUpdateRequest, id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/password/{id}")
    public ResponseEntity<UserResponse> updateUserPassword(@PathVariable long id, @Valid @RequestBody UserPasswordUpdateRequest userPasswordUpdateRequest) {
        UserResponse user = userService.updatePassword(userPasswordUpdateRequest, id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long id) {
        userService.delete(id);
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserResponse> searchByEmail(@NotBlank @RequestParam String email) {
        UserResponse user = userService.searchByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/by-name")
    public ResponseEntity<List<UserResponse>> searchByName(@NotBlank @RequestParam String name) {
        List<UserResponse> users = userService.searchByName(name);
        return ResponseEntity.ok(users);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> listAll() {
        List<UserResponse> users = userService.listAll();
        return ResponseEntity.ok(users);
    }
}
