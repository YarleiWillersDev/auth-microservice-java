package br.com.confidence.service.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.confidence.dto.role.RoleRequest;
import br.com.confidence.dto.role.RoleResponse;
import br.com.confidence.exception.role.InvalidRoleNameException;
import br.com.confidence.model.role.Role;
import br.com.confidence.repository.role.RoleRepository;
import br.com.confidence.updater.role.RoleUpdater;
import br.com.confidence.validation.role.RoleValidation;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTestIT {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleValidation roleValidation;

    @Mock
    private RoleUpdater roleUpdater;

    private RoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        roleService = new RoleServiceImpl(roleRepository, roleValidation, roleUpdater);
    }

    @Nested
    class CreateTest {

        @Test
        void shouldCreateRoleWhenReceivedDataIsCorrect() {
            
            RoleRequest request = new RoleRequest("ADMIN", "Admin role");
            Role savedRole = new Role();
            savedRole.setId(1L);
            savedRole.setName("ADMIN");
            savedRole.setDescription("Admin role");

            when(roleRepository.save(any(Role.class))).thenReturn(savedRole);

            RoleResponse response = roleService.create(request);

            verify(roleValidation).validateRoleRequestInformation(request);
            verify(roleRepository).save(any(Role.class));

            assertEquals(savedRole.getId(), response.id());
            assertEquals(savedRole.getName(), response.name());
            assertEquals(savedRole.getDescription(), response.description());

        }

        @Test
        void shouldThrowExceptionWhenTryingCreateRoleWithNullName() {

            RoleRequest request = new RoleRequest(null, "Admin role");
            
            doThrow(new InvalidRoleNameException("Role name cannot be null/empty or contain fewer than 3 letters."))
                .when(roleValidation).validateRoleRequestInformation(request);

            assertThrows(InvalidRoleNameException.class, () -> roleService.create(request));
            verifyNoInteractions(roleRepository);
        }

    }

}
