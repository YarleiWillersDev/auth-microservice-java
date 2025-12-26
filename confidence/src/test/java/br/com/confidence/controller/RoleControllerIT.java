package br.com.confidence.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import br.com.confidence.dto.role.RoleUpdateRequest;
import br.com.confidence.model.role.Role;

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

    @Nested
    class updateRoleTest {

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnStatus200WhenAttemptingUpdateRole() throws Exception {
            Role role = createAdminRoleForTest();

            long roleID = role.getId();

            RoleUpdateRequest newRole = new RoleUpdateRequest("LITTLE BOSS", "Role with LITTLE BOSS permission");

            String requestBodyJson = objectMapper.writeValueAsString(newRole);

            mockMvc.perform(put("/roles/{id}", roleID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyJson))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").value("LITTLE BOSS"))
                    .andExpect(jsonPath("$.description").value("Role with LITTLE BOSS permission"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnStatus200WhenTryingUpdateRoleWithNullName() throws Exception {
            Role role = createAdminRoleForTest();

            long roleID = role.getId();

            RoleUpdateRequest roleUpdateRequest = new RoleUpdateRequest(null, "Role with LITTLE BOSS permission");

            String requestBodyJson = objectMapper.writeValueAsString(roleUpdateRequest);

            mockMvc.perform(put("/roles/{id}", roleID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyJson))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnStatus200WhenTryingUpdateRolewithoutChangingBame() throws Exception {
            Role role = new Role();
            role.setName("BOSS");
            role.setDescription("Role with BOSS permission");
            roleRepository.save(role);

            long roleID = role.getId();

            RoleUpdateRequest roleUpdateRequest = new RoleUpdateRequest("BOSS", "Role with LITTLE BOSS permission");

            String requestBodyJson = objectMapper.writeValueAsString(roleUpdateRequest);

            mockMvc.perform(put("/roles/{id}", roleID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyJson))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnStatus400WhenTryingUpdateRoleWithEmptyName() throws Exception {
            Role role = createAdminRoleForTest();

            long roleID = role.getId();

            RoleUpdateRequest roleUpdateRequest = new RoleUpdateRequest("", "Role with LITTLE BOSS permission");

            String requestBodyJson = objectMapper.writeValueAsString(roleUpdateRequest);

            mockMvc.perform(put("/roles/{id}", roleID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyJson))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnStatus404WhenTryingUpdateRoleWithNonExistentID() throws Exception {
            long roleID = 999L;

            RoleUpdateRequest roleUpdateRequest = new RoleUpdateRequest("LITTLE BOSS", "Role with LITTLE BOSS permission");

            String requestBodyJson = objectMapper.writeValueAsString(roleUpdateRequest);

            mockMvc.perform(put("/roles/{id}", roleID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyJson))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @WithMockUser(roles = "USER")
        void shouldReturnStatus403WhenAttemptingUpdateRoleWithUnauthorizedUser() throws Exception {
            Role role = createAdminRoleForTest();

            long roleID = role.getId();

            RoleUpdateRequest roleUpdateRequest = new RoleUpdateRequest("LITTLE BOSS", "Role with LITTLE BOSS permission");

            String requestBodyJson = objectMapper.writeValueAsString(roleUpdateRequest);

            mockMvc.perform(put("/roles/{id}", roleID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyJson))
                    .andDo(print())
                    .andExpect(status().isForbidden());     
        }

        @Test
        void shouldReturnStatus403WhenAttemptingUpdateRoleWithUnauthenticatedUser() throws Exception {
            Role role = createAdminRoleForTest();

            long roleID = role.getId();

            RoleUpdateRequest roleUpdateRequest = new RoleUpdateRequest("LITTLE BOSS", "Role with LITTLE BOSS permission");

            String requestBodyJson = objectMapper.writeValueAsString(roleUpdateRequest);

            mockMvc.perform(put("/roles/{id}", roleID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyJson))
                    .andDo(print())
                    .andExpect(status().isForbidden());   
        }
        

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnStatus409WhenTryingUpdateRoleWithAlreadyExistingName() throws Exception {

            Role role = new Role();
            role.setName("ADMIN");
            role.setDescription("ADMIN role");
            roleRepository.save(role);

            Role role2 = new Role();
            role2.setName("BOSS");
            role2.setDescription("BOSS role");
            roleRepository.save(role2);

            long roleID = role2.getId();

            RoleUpdateRequest roleUpdateRequest = new RoleUpdateRequest("ADMIN", "Role with LITTLE BOSS permission");

            String requestBodyJson = objectMapper.writeValueAsString(roleUpdateRequest);

            mockMvc.perform(put("/roles/{id}", roleID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBodyJson))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.status").value(409));

        }
    }
}
