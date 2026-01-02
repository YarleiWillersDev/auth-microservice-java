package br.com.confidence.controller;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.confidence.model.role.Role;
import br.com.confidence.model.user.User;
import br.com.confidence.repository.auth.PasswordResetTokenRepository;
import br.com.confidence.repository.role.RoleRepository;
import br.com.confidence.repository.user.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(BaseIntegrationTests.TestMailConfig.class)
public abstract class BaseIntegrationTests {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordResetTokenRepository passwordResetTokenRepository;

    @AfterEach
    void tearDown() {
        passwordResetTokenRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    protected Role createAdminRoleForTest() {
        Role role = new Role();
        role.setName("ADMIN");
        role.setDescription("ADMIN permissions");
        return roleRepository.save(role);
    } 

    protected Role createUserRoleForTest() {
        Role role = new Role();
        role.setName("USER");
        role.setDescription("USER permissions");
        return roleRepository.save(role);
    }

    protected User createAdminUserForTest() {
        User user = new User();
        user.setName("Yarlei");
        user.setEmail("admin@gmail.com");
        user.setPassword("@SenhaSegura123");

        List<Role> roles = new ArrayList<>();
        roles.add(createAdminRoleForTest());
        user.setRoles(roles);

        return userRepository.save(user);
    }

    protected User createNormalUserForTest() {
        User user = new User();
        user.setName("Yarlei");
        user.setEmail("user@gmail.com");
        user.setPassword("@SenhaSegura123");

        List<Role> roles = new ArrayList<>();
        roles.add(createUserRoleForTest());
        user.setRoles(roles);

        return userRepository.save(user);
    }

    @TestConfiguration
    static class TestMailConfig {

        @Bean
        public JavaMailSender javaMailSender() {
            return mock(JavaMailSender.class);
        }
    }
}
