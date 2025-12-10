package br.com.confidence.service.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.confidence.dto.role.RoleRequest;
import br.com.confidence.dto.role.RoleResponse;
import br.com.confidence.dto.role.RoleUpdateRequest;
import br.com.confidence.exception.role.InvalidRoleNameException;
import br.com.confidence.exception.role.RoleAlreadyExistsException;
import br.com.confidence.exception.role.RoleNotFoundException;
import br.com.confidence.model.role.Role;
import br.com.confidence.repository.role.RoleRepository;
import br.com.confidence.updater.role.RoleUpdater;
import br.com.confidence.validation.role.RoleValidation;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

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

        @Test
        void shouldThrowExceptionWhenTryingCreateRoleWithEmptyName() {

            RoleRequest request = new RoleRequest("", "Admin role");

            doThrow(new InvalidRoleNameException("Role name cannot be null/empty or contain fewer than 3 letters."))
                    .when(roleValidation).validateRoleRequestInformation(request);

            assertThrows(InvalidRoleNameException.class, () -> roleService.create(request));
            verifyNoInteractions(roleRepository);
        }

        @Test
        void shouldThrowExceptionWhenTryingCreateRoleWithInvalidName() {

            RoleRequest request = new RoleRequest("jo", "Admin role");

            doThrow(new InvalidRoleNameException("Role name cannot be null/empty or contain fewer than 3 letters."))
                    .when(roleValidation).validateRoleRequestInformation(request);

            assertThrows(InvalidRoleNameException.class, () -> roleService.create(request));
            verifyNoInteractions(roleRepository);
        }

        @Test
        void shouldThrowExceptionWhenTryingCreateRoleWithExistingName() {

            RoleRequest request = new RoleRequest("ADMIN", "Admin role");

            when(roleRepository.existsByName("ADMIN")).thenReturn(true);

            assertThrows(RoleAlreadyExistsException.class, () -> roleService.create(request));
            verify(roleValidation).validateRoleRequestInformation(request);
            verify(roleRepository, never()).save(any(Role.class));

        }
    }

    @Nested
    class UpdateTest {

        @Test
        void shouldUpdateRoleWhenDataIsValid() {
            long id = 1L;

            RoleUpdateRequest request = new RoleUpdateRequest(
                    Optional.of("NEW_ADMIN"),
                    Optional.of("New description"));

            Role existingRole = new Role();
            existingRole.setId(id);
            existingRole.setName("ADMIN");
            existingRole.setDescription("Admin Role");

            when(roleRepository.findById(id)).thenReturn(Optional.of(existingRole));
            when(roleRepository.save(existingRole)).thenReturn(existingRole);

            doAnswer(invocation -> {
                Role roleArg = invocation.getArgument(0, Role.class);
                RoleUpdateRequest reqArg = invocation.getArgument(1, RoleUpdateRequest.class);

                reqArg.name().ifPresent(roleArg::setName);
                reqArg.description().ifPresent(roleArg::setDescription);

                return null;
            }).when(roleUpdater).updateRoleInformation(existingRole, request);

            RoleResponse response = roleService.update(request, id);

            verify(roleValidation).validateRoleUpdateRequestInformation(request);
            verify(roleUpdater).updateRoleInformation(existingRole, request);
            verify(roleRepository).save(existingRole);

            assertEquals(id, response.id());
            assertEquals("NEW_ADMIN", response.name());
            assertEquals("New description", response.description());

        }

        @Test
        void shouldThrowExceptionWhenRoleNotFoundOnUpdate() {

            long id = 999L;

            RoleUpdateRequest request = new RoleUpdateRequest(
                    Optional.of("NEW_ADMIN"),
                    Optional.of("New description"));

            when(roleRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(RoleNotFoundException.class, () -> roleService.update(request, id));

            verifyNoInteractions(roleValidation);
            verifyNoInteractions(roleUpdater);
            verify(roleRepository, never()).save(any(Role.class));
        }

        @Test
        void shouldThrowInvalidRoleNameExceptionWhenUpdateWithInvalideName() {

            long id = 1;

            RoleUpdateRequest request = new RoleUpdateRequest(
                    Optional.of("jo"),
                    Optional.of("New description"));

            Role existingRole = new Role();
            existingRole.setId(id);
            existingRole.setName("ADMIN");
            existingRole.setDescription("Admin Role");

            when(roleRepository.findById(id)).thenReturn(Optional.of(existingRole));

            doThrow(new InvalidRoleNameException("Role name cannot be null/empty or contain fewer than 3 letters."))
                    .when(roleValidation).validateRoleUpdateRequestInformation(request);

            assertThrows(InvalidRoleNameException.class, () -> roleService.update(request, id));

            verifyNoInteractions(roleUpdater);
            verify(roleRepository, never()).save(any(Role.class));
        }

    }

    @Nested
    class DeleteTest {

        @Test
        void shouldDeleteUserUsingValidId() {

            long id = 1L;

            Role existingRole = new Role();
            existingRole.setId(id);

            when(roleRepository.findById(id)).thenReturn(Optional.of(existingRole));

            roleService.delete(id);

            verify(roleRepository).findById(id);
            verify(roleRepository).delete(existingRole);
        }

        @Test
        void shouldThrowExceptionWhenDeletingNonExistingRole() {

            long id = 999L;

            when(roleRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(RoleNotFoundException.class, () -> roleService.delete(id));

            verify(roleRepository).findById(id);
            verify(roleRepository, never()).delete(any(Role.class));

        }
    }

    @Nested
    class searchById {

        @Test
        void shouldReturnRoleFoundThroughId() {

            long id = 1L;

            Role savedRole = new Role();
            savedRole.setId(id);
            savedRole.setName("ADMIN");
            savedRole.setDescription("Admin role");

            when(roleRepository.findById(id)).thenReturn(Optional.of(savedRole));

            RoleResponse response = roleService.searchById(id);

            verify(roleRepository).findById(id);
            assertEquals(id, response.id());
            assertEquals("ADMIN", response.name());
            assertEquals("Admin role", response.description());
        }

        @Test
        void shouldReturnExceptionIdicatingRoleNotFoundWithUnregisteredId() {

            long id = 999L;

            when(roleRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(RoleNotFoundException.class, () -> roleService.searchById(id));

            verify(roleRepository).findById(id);
        }
    }

    @Nested
    class searchByName {

        @Test
        void shouldReturnRoleListWhenNameMatches() {
            String name = "adm";

            Role role1 = new Role();
            role1.setId(1L);
            role1.setName("ADMIN");
            role1.setDescription("Admin role");

            Role role2 = new Role();
            role2.setId(2L);
            role2.setName("ADVANCED_ADMIN");
            role2.setDescription("Advanced admin role");

            List<Role> roles = List.of(role1, role2);

            when(roleRepository.findByNameContainingIgnoreCase(name)).thenReturn(roles);

            List<RoleResponse> responses = roleService.searchByName(name);

            verify(roleRepository).findByNameContainingIgnoreCase(name);
            assertEquals(2, responses.size());
            assertEquals("ADMIN", responses.get(0).name());
        }

        @Test
        void shouldReturnEmptyListWhenNoRoleMatchesName() {
            String name = "does-not-exist";

            when(roleRepository.findByNameContainingIgnoreCase(name))
                    .thenReturn(Collections.emptyList());

            List<RoleResponse> responses = roleService.searchByName(name);

            verify(roleRepository).findByNameContainingIgnoreCase(name);
            assertTrue(responses.isEmpty());
        }
    }

    @Nested
    class ListAll {

        @Test
        void shouldReturnAllRolesWhenListIsNotEmpty() {
            Role role1 = new Role();
            role1.setId(1L);
            role1.setName("ADMIN");
            role1.setDescription("Admin role");

            Role role2 = new Role();
            role2.setId(2L);
            role2.setName("USER");
            role2.setDescription("User role");

            List<Role> roles = List.of(role1, role2);

            when(roleRepository.findAll()).thenReturn(roles);

            List<RoleResponse> responses = roleService.listAll();

            verify(roleRepository).findAll();
            assertEquals(2, responses.size());
            assertEquals("ADMIN", responses.get(0).name());
            assertEquals("USER", responses.get(1).name());
        }

        @Test
        void shouldReturnEmptyListWhenNoRolesExist() {
            when(roleRepository.findAll()).thenReturn(Collections.emptyList());

            List<RoleResponse> responses = roleService.listAll();

            verify(roleRepository).findAll();
            assertTrue(responses.isEmpty());
        }

    }
}
