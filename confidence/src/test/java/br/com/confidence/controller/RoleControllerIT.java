package br.com.confidence.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.confidence.dto.role.RoleRequest;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class RoleControllerIT {

    @Nested
    class createRoleTest {

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnStatus201WhenCreatingRoleSuccessfully() {

            RoleRequest roleRequest = new RoleRequest("ADMIN", "Role with ADMIN permission");

            String requestBodyJson = objectMapper

        }

    }

}
