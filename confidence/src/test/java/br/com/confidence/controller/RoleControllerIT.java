package br.com.confidence.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import br.com.confidence.dto.role.RoleRequest;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class RoleControllerIT extends BaseIntegrationTests {

    @Nested
    class createRoleTest {

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnStatus201WhenCreatingRoleSuccessfully() throws Exception {

            RoleRequest roleRequest = new RoleRequest("BOSS", "Role with BOSS permission");

            String requestBodyJson = objectMapper.writeValueAsString(roleRequest);

            mockMvc.perform(post("/roles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyJson))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").value("BOSS"))
                    .andExpect(jsonPath("$.description").value("Role with BOSS permission"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnStatus400WhenTryingCreateRoleWithEmptyName() throws Exception {

            RoleRequest roleRequest = new RoleRequest("", "Role with ADMIN permission");

            String requestBodyJson = objectMapper.writeValueAsString(roleRequest);

            mockMvc.perform(post("/roles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyJson))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnStatus400WhenTryingCreateRoleWithNullName() throws Exception {

            RoleRequest roleRequest = new RoleRequest(null, "Role with ADMIN permission");

            String requestBodyJson = objectMapper.writeValueAsString(roleRequest);

            mockMvc.perform(post("/roles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyJson))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        void shouldReturnStatus403WhenUserDoesNotHaveAdminRole() throws Exception {

            RoleRequest roleRequest = new RoleRequest("BOSS", "Role with BOSS permission");

            String requestBodyJson = objectMapper.writeValueAsString(roleRequest);

            mockMvc.perform(post("/roles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyJson))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnStatus409WhenTryingCreateRoleWithAlreadyExistingName() throws Exception {

            RoleRequest roleRequest = new RoleRequest("ADMIN", "Role with ADMIN permission");

            String requestBodyJson = objectMapper.writeValueAsString(roleRequest);

            mockMvc.perform(post("/roles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyJson))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.message").exists());
        }
    }

}
