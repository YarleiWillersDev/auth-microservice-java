package br.com.confidence.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import br.com.confidence.dto.user.UserEmailUpdateRequest;
import br.com.confidence.dto.user.UserPasswordUpdateRequest;
import br.com.confidence.dto.user.UserRequest;
import br.com.confidence.dto.user.UserUpdateRequest;
import br.com.confidence.model.user.User;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserControllerIT extends BaseIntegrationTests {

        @Nested
        class createUserTest {

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus201WhenUserIsCreatedSuccessfully() throws Exception {
                        createUserRoleForTest();

                        UserRequest userRequest = new UserRequest("Test", "test12345@email.com", "@SenhaSegura123");

                        String requestBodyJson = objectMapper.writeValueAsString(userRequest);

                        mockMvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBodyJson))
                                        .andDo(print())
                                        .andExpect(status().isCreated())
                                        .andExpect(jsonPath("$.id").exists())
                                        .andExpect(jsonPath("$.name").value(userRequest.name()))
                                        .andExpect(jsonPath("$.email").value(userRequest.email()))
                                        .andExpect(jsonPath("$.roles").isArray())
                                        .andExpect(jsonPath("$.roles[0].name").value("USER"));
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenCreatingUserWithEmptyName() throws Exception {
                        UserRequest userRequest = new UserRequest("", "test12345@email.com", "@SenhaSegura123");

                        String requestBodyJson = objectMapper.writeValueAsString(userRequest);

                        mockMvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBodyJson))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());

                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenCreatingUserWithNullName() throws Exception {
                        UserRequest userRequest = new UserRequest(null, "test12345@email.com", "@SenhaSegura123");

                        String requestBodyJson = objectMapper.writeValueAsString(userRequest);

                        mockMvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBodyJson))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenCreatingUserWithTooLongName() throws Exception {
                        createUserRoleForTest();
                        String longName = "A".repeat(256);

                        UserRequest userRequest = new UserRequest(longName, "test12345@email.com", "@SenhaSegura123");

                        String requestBodyJson = objectMapper.writeValueAsString(userRequest);

                        mockMvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBodyJson))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenCreatingUserWithEmaillEmpty() throws Exception {
                        UserRequest userRequest = new UserRequest("Test", "", "@SenhaSegura123");

                        String requestBodyJson = objectMapper.writeValueAsString(userRequest);

                        mockMvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBodyJson))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenCreatingUserWithEmailNull() throws Exception {
                        UserRequest userRequest = new UserRequest("Test", null, "@SenhaSegura123");

                        String requestBodyJson = objectMapper.writeValueAsString(userRequest);

                        mockMvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBodyJson))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenCreatingUserWithInvalidEmail() throws Exception {
                        createUserRoleForTest();
                        UserRequest userRequest = new UserRequest("Test", "invalid-email", "@SenhaSegura123");

                        String requestBodyJson = objectMapper.writeValueAsString(userRequest);

                        mockMvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBodyJson))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus409WhenCreatingUserWithDuplicateEmail() throws Exception {
                        createUserRoleForTest();
                        UserRequest firstUser = new UserRequest("Test1", "test12345@email.com", "@SenhaSegura123");

                        String requestBodyJson1 = objectMapper.writeValueAsString(firstUser);

                        mockMvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBodyJson1))
                                        .andDo(print())
                                        .andExpect(status().isCreated());

                        UserRequest duplicateUser = new UserRequest("Test2", "test12345@email.com", "@SenhaSegura123");

                        String requestBodyJson2 = objectMapper.writeValueAsString(duplicateUser);

                        mockMvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBodyJson2))
                                        .andDo(print())
                                        .andExpect(status().isConflict());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenCreatingUserWithEmptyPassword() throws Exception {
                        UserRequest userRequest = new UserRequest("Test", "test12345@email.com", "");

                        String requestBodyJson = objectMapper.writeValueAsString(userRequest);

                        mockMvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBodyJson))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenCreatingUserWithNullPassword() throws Exception {
                        UserRequest userRequest = new UserRequest("Test", "test12345@email.com", null);

                        String requestBodyJson = objectMapper.writeValueAsString(userRequest);

                        mockMvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBodyJson))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenCreatingUserWithPasswordWithoutNumbers() throws Exception {
                        UserRequest userRequest = new UserRequest("Test", "test12345@email.com", "SenhaNaoSegura");

                        String requestBodyJson = objectMapper.writeValueAsString(userRequest);

                        mockMvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBodyJson))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenCreatingUserWithPasswordWithoutUppercaseLetter() throws Exception {
                        UserRequest userRequest = new UserRequest("Test", "test12345@email.com", "@senhasegura123");

                        String requestBodyJson = objectMapper.writeValueAsString(userRequest);

                        mockMvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBodyJson))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenCreatingUserWithPasswordWithoutLowerLetter() throws Exception {
                        UserRequest userRequest = new UserRequest("Test", "test12345@email.com", "@SENHASEGURA123");

                        String requestBodyJson = objectMapper.writeValueAsString(userRequest);

                        mockMvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBodyJson))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenCreatingUserWithPasswordWithoutSpecialCharacter() throws Exception {
                        UserRequest userRequest = new UserRequest("Test", "test12345@email.com", "SenhaSegura12345");

                        String requestBodyJson = objectMapper.writeValueAsString(userRequest);

                        mockMvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBodyJson))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "USER")
                void shouldReturnStatus403WhenTryingToCreateUserWithUnauthorizedUser() throws Exception {
                        UserRequest userRequest = new UserRequest("Test", "test12345@email.com", "@SenhaSegura12345");

                        String requestBodyJson = objectMapper.writeValueAsString(userRequest);

                        mockMvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBodyJson))
                                        .andDo(print())
                                        .andExpect(status().isForbidden());
                }

                @Test
                void shouldReturnStatus403WhenTryingToCreateUserWithUserWithoutAuthentication() throws Exception {
                        UserRequest userRequest = new UserRequest("Test", "test12345@email.com", "@SenhaSegura12345");

                        String requestBodyJson = objectMapper.writeValueAsString(userRequest);

                        mockMvc.perform(post("/users")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBodyJson))
                                        .andDo(print())
                                        .andExpect(status().isForbidden());
                }
        }

        @Nested
        class updateUserTest {

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus200WhenUpdatingUserSuccessfully() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("Test");

                        mockMvc.perform(put("/users/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id").exists())
                                        .andExpect(jsonPath("$.name").value("Test"));
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus200WhenUpdatingUserWithSameName() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();
                        String originalName = user.getName();

                        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(originalName);

                        mockMvc.perform(put("/users/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.name").value(originalName));
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenUpdatingUserWithEmptyName() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("");

                        mockMvc.perform(put("/users/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenUpdatingUserWithNullName() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(null);

                        mockMvc.perform(put("/users/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenTryingToUpdateUserWithNameExceedingCharacterLimit() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();
                        String longName = "A".repeat(256);

                        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(longName);

                        mockMvc.perform(put("/users/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "USER")
                void shouldReturnStatus403WhenTryingToUpdateUserWithUnauthorizedUser() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("Alonso");

                        mockMvc.perform(put("/users/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isForbidden());
                }

                @Test
                void shouldReturnStatus403WhenTryingToUpdateUserWithUserNotAuthenticated() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("Alonso");

                        mockMvc.perform(put("/users/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isForbidden());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus404WhenTryingToUpdateUserWithIdNotRegisteredInTheDatabase() throws Exception {
                        long userID = 999L;

                        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("Alonso");

                        mockMvc.perform(put("/users/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isNotFound());
                }
        }

        @Nested
        class updateUserEmailTest {

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus200WhenSuccessfullyAttemptingToUpdateUserEmail() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserEmailUpdateRequest userEmailUpdateRequest = new UserEmailUpdateRequest(
                                        "TestTest@email.com.br");

                        mockMvc.perform(patch("/users/email/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userEmailUpdateRequest)))
                                        .andDo(print())

                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id").exists())
                                        .andExpect(jsonPath("$.name").value(user.getName()))
                                        .andExpect(jsonPath("$.email").value("TestTest@email.com.br"));
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus200WhenTryingToUpdateUserEmailWithoutChangingEmailValue() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserEmailUpdateRequest userEmailUpdateRequest = new UserEmailUpdateRequest("admin@gmail.com");

                        mockMvc.perform(patch("/users/email/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userEmailUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id").exists())
                                        .andExpect(jsonPath("$.name").value(user.getName()))
                                        .andExpect(jsonPath("$.email").value("admin@gmail.com"));
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenTryingToUpdateEmailWithNullData() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserEmailUpdateRequest userEmailUpdateRequest = new UserEmailUpdateRequest(null);

                        mockMvc.perform(patch("/users/email/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userEmailUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenTryingToUpdateEmailWithEmptyString() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserEmailUpdateRequest userEmailUpdateRequest = new UserEmailUpdateRequest("");

                        mockMvc.perform(patch("/users/email/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userEmailUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenTryingToUpdateEmailWithMinimumCharacters() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserEmailUpdateRequest userEmailUpdateRequest = new UserEmailUpdateRequest("user@email.com");

                        mockMvc.perform(patch("/users/email/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userEmailUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenTryingToUpdateEmailWithInvalidFormat() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserEmailUpdateRequest userEmailUpdateRequest = new UserEmailUpdateRequest(
                                        "emailInvalido.com.br");

                        mockMvc.perform(patch("/users/email/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userEmailUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "USER")
                void shouldReturnStatus403WhenTryingToUpdateEmailWithUnauthorizedUser() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserEmailUpdateRequest userEmailUpdateRequest = new UserEmailUpdateRequest("admin@gmail.com");

                        mockMvc.perform(patch("/users/email/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userEmailUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isForbidden());
                }

                @Test
                void shouldReturnStatus403WhenTryingToUpdateEmailWithUnauthenticatedUser() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserEmailUpdateRequest userEmailUpdateRequest = new UserEmailUpdateRequest("admin@gmail.com");

                        mockMvc.perform(patch("/users/email/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userEmailUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isForbidden());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus404WhenTryingToUpdateUserWithIdNotRegisteredInTheDatabase() throws Exception {
                        long userID = 999L;

                        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("Alonso");

                        mockMvc.perform(put("/users/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isNotFound());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus404WhenTryingToUpdateEmailWithNonExistentUserId() throws Exception {
                        createAdminUserForTest();
                        User user = createNormalUserForTest();
                        long userID = user.getId();

                        UserEmailUpdateRequest userEmailUpdateRequest = new UserEmailUpdateRequest("admin@gmail.com");

                        mockMvc.perform(patch("/users/email/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userEmailUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isConflict());
                }
        }

        @Nested
        class updateUserPasswordTest {

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus200WhenUpdatingUserPasswordWithCorrectData() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserPasswordUpdateRequest userPasswordUpdateRequest = new UserPasswordUpdateRequest(
                                        "@SenhaSegura123",
                                        "!SenhaSegura3000");

                        mockMvc.perform(patch("/users/password/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userPasswordUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id").exists())
                                        .andExpect(jsonPath("$.name").value(user.getName()));
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenUpdatingUserPasswordWithNullPassword() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserPasswordUpdateRequest userPasswordUpdateRequest = new UserPasswordUpdateRequest(
                                        "@SenhaSegura123",
                                        null);

                        mockMvc.perform(patch("/users/password/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userPasswordUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());

                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenUpdatingUserPasswordWithEmptyPassword() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserPasswordUpdateRequest userPasswordUpdateRequest = new UserPasswordUpdateRequest(
                                        "@SenhaSegura123", "");

                        mockMvc.perform(patch("/users/password/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userPasswordUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenUpdatingUserPasswordWithPasswordWithoutUppercaseLetter()
                                throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserPasswordUpdateRequest userPasswordUpdateRequest = new UserPasswordUpdateRequest(
                                        "@SenhaSegura123",
                                        "!senhasegura123");

                        mockMvc.perform(patch("/users/password/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userPasswordUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenUpdatingUserPasswordWithPasswordWithoutLowerLetter() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserPasswordUpdateRequest userPasswordUpdateRequest = new UserPasswordUpdateRequest(
                                        "@SenhaSegura123",
                                        "!SENHASEGURA123");

                        mockMvc.perform(patch("/users/password/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userPasswordUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenUpdatingUserPasswordWithPasswordWithoutNumbers() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserPasswordUpdateRequest userPasswordUpdateRequest = new UserPasswordUpdateRequest(
                                        "@SenhaSegura123",
                                        "SenhaSegura@@@");

                        mockMvc.perform(patch("/users/password/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userPasswordUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenUpdatingUserPasswordWithPasswordWithoutSymbol() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserPasswordUpdateRequest userPasswordUpdateRequest = new UserPasswordUpdateRequest(
                                        "@SenhaSegura123",
                                        "SenhaSegura1234");

                        mockMvc.perform(patch("/users/password/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userPasswordUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenUpdatingUserPasswordWithPasswordBelowMinimumCharacters()
                                throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserPasswordUpdateRequest userPasswordUpdateRequest = new UserPasswordUpdateRequest(
                                        "@SenhaSegura123",
                                        "@23456789Aa");

                        mockMvc.perform(patch("/users/password/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userPasswordUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "USER")
                void shouldReturnStatus400WhenUpdatingUserPasswordWithUnauthorizedUser() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserPasswordUpdateRequest userPasswordUpdateRequest = new UserPasswordUpdateRequest(
                                        "@SenhaSegura123",
                                        "!SenhaSegura3000");

                        mockMvc.perform(patch("/users/password/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userPasswordUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isForbidden());
                }

                @Test
                void shouldReturnStatus400WhenUpdatingUserPasswordWithUserWithoutAuthentication() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        UserPasswordUpdateRequest userPasswordUpdateRequest = new UserPasswordUpdateRequest(
                                        "@SenhaSegura123",
                                        "!SenhaSegura3000");

                        mockMvc.perform(patch("/users/password/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userPasswordUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isForbidden());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus404WhenUpdatingUserPasswordWithNoExistentId() throws Exception {
                        long falseID = 999L;

                        UserPasswordUpdateRequest userPasswordUpdateRequest = new UserPasswordUpdateRequest(
                                        "@SenhaSegura123",
                                        "!SenhaSegura3000");

                        mockMvc.perform(patch("/users/password/{id}", falseID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userPasswordUpdateRequest)))
                                        .andDo(print())
                                        .andExpect(status().isNotFound());
                }
        }

        @Nested
        class deleteUserTest {

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus204WhenUserIsSuccessfullyDeleted() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        mockMvc.perform(delete("/users/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isNoContent());
                }

                @Test
                @WithMockUser(roles = "USER")
                void shouldReturnStatus403WhenTryingToDeleteUserWithUnauthorizedUser() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        mockMvc.perform(delete("/users/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isForbidden());
                }

                @Test
                void shouldReturnStatus403WhenTryingToDeleteUserWithUserWithoutAuthentication() throws Exception {
                        User user = createAdminUserForTest();
                        long userID = user.getId();

                        mockMvc.perform(delete("/users/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isForbidden());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus404WhenTryingToDeleteUserWithInvalidId() throws Exception {
                        long userID = 999L;

                        mockMvc.perform(delete("/users/{id}", userID)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isNotFound());
                }
        }

        @Nested
        class searchByEmailUserTest {

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus200WhenBuscarUsuarioPorEmailComDadosCorretos() throws Exception {
                        User user = createAdminUserForTest();

                        mockMvc.perform(get("/users/by-email")
                                        .param("email", user.getEmail())
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id").exists())
                                        .andExpect(jsonPath("$.name").value(user.getName()))
                                        .andExpect(jsonPath("$.email").value(user.getEmail()));
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void mustReturnStatus400WhenSearchUserByEmailWithEmailEmpty() throws Exception {
                        createAdminUserForTest();
                        String email = "";

                        mockMvc.perform(get("/users/by-email")
                                        .param("email", email)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "USER")
                void shouldReturnStatus403WhenSearchingForUserByEmailWithUnauthorizedUser() throws Exception {
                        User user = createAdminUserForTest();

                        mockMvc.perform(get("/users/by-email")
                                        .param("email", user.getEmail())
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isForbidden());
                }

                @Test
                void shouldReturnStatus403WhenSearchingForUserByEmailWithUnauthenticatedUser() throws Exception {
                        User user = createAdminUserForTest();

                        mockMvc.perform(get("/users/by-email")
                                        .param("email", user.getEmail())
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isForbidden());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus404WhenSearchingForUserByEmailWithNotRegisteredEmail() throws Exception {

                        mockMvc.perform(get("/users/by-email")
                                        .param("email", "TesteEmail@email.com")
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isNotFound());
                }
        }

        @Nested
        class searchByNameUserTest {

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus200WhenSearchingForUserByNameWithValidData() throws Exception {
                        User user = createAdminUserForTest();
                        String name = user.getName();

                        mockMvc.perform(get("/users/by-name")
                                        .param("name", name)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$").isArray())
                                        .andExpect(jsonPath("$").isNotEmpty());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus200WithEmptyListWhenSearchingForUserWithUnregisteredName() throws Exception {
                        createAdminUserForTest();
                        String name = "SELLER";

                        mockMvc.perform(get("/users/by-name")
                                        .param("name", name)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(jsonPath("$").isArray())
                                        .andExpect(jsonPath("$").isEmpty());
                }

                @Test
                @WithMockUser(roles = "ADMIN")
                void shouldReturnStatus400WhenSearchingForUserWithEmptyNameReturningListWithAllUsers()
                                throws Exception {
                        createAdminRoleForTest();
                        String name = "";

                        mockMvc.perform(get("/users/by-name")
                                        .param("name", name)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @WithMockUser(roles = "USER")
                void shouldReturnStatus403WhenSearchingForUserWithUnauthorizedUser() throws Exception {
                        createAdminRoleForTest();
                        String name = "ADMIN";

                        mockMvc.perform(get("/users/by-name")
                                        .param("name", name)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isForbidden());
                }

                @Test
                void shouldReturnStatus403WhenSearchingForUserWithUnauthenticatedUser() throws Exception {
                        createAdminRoleForTest();
                        String name = "ADMIN";

                        mockMvc.perform(get("/users/by-name")
                                        .param("name", name)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isForbidden());
                }

        }

        @Nested
        class listAllUserTest {

                @Test
                @WithMockUser(roles = "ADMIN")
                void deveRetornarStatus200AoListarTodosUsuarios() throws Exception {
                        createAdminRoleForTest();

                        mockMvc.perform(get("/users")
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$").isArray())
                                        .andExpect(jsonPath("$").isArray())
                                        .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
                }

                @Test
                @WithMockUser(roles = "USER")
                void deveRetornarStatus403AoListarTodosUsuariosComUsuarioSemAutorizacao() throws Exception {
                        createAdminRoleForTest();

                        mockMvc.perform(get("/users")
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isForbidden());
                }

                @Test
                void deveRetornarStatus403AoListarTodosUsuariosComUsuarioSemAutenticacao() throws Exception {
                        createAdminRoleForTest();

                        mockMvc.perform(get("/users")
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andDo(print())
                                        .andExpect(status().isForbidden());
                }
        }
}
