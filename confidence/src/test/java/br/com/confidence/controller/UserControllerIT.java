package br.com.confidence.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import br.com.confidence.dto.user.UserRequest;

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

}
