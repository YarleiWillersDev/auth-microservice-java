package br.com.confidence.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import br.com.confidence.dto.authentication.AuthenticationRequest;
import br.com.confidence.dto.authentication.RegisterRequest;

public class AuthControllerIT extends BaseIntegrationTests {

	@Nested
	class registerAuthTest {

		@Test
		void shouldReturnStatus201WhenUserRegistersSuccessfully() throws Exception {
			createUserRoleForTest();

			RegisterRequest registerRequest = new RegisterRequest(
					"ADMIN_USER",
					"admin.user@email.com",
					"SenhaSegura@123");

			mockMvc.perform(post("/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(registerRequest)))
					.andDo(print())
					.andExpect(status().isCreated())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.id").exists())
					.andExpect(jsonPath("$.email").value("admin.user@email.com"));
		}

		@Test
		void shouldReturnStatus400WhenRegisteringUserWithEmptyName() throws Exception {

			RegisterRequest registerRequest = new RegisterRequest(
					"",
					"admin.user@email.com",
					"SenhaSegura@123");

			mockMvc.perform(post("/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(registerRequest)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus400WhenRegisteringUserWithNameNull() throws Exception {

			RegisterRequest registerRequest = new RegisterRequest(
					null,
					"admin.user@email.com",
					"SenhaSegura@123");

			mockMvc.perform(post("/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(registerRequest)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus400WhenRegisteringUserWithInvalidEmailFormat() throws Exception {

			RegisterRequest registerRequest = new RegisterRequest(
					"ADMIN_USER",
					"admin.user.email.com",
					"SenhaSegura@123");

			mockMvc.perform(post("/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsBytes(registerRequest)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void mustReturnStatus400WhenRegisteringUserWithEmptyEmail() throws Exception {

			RegisterRequest registerRequest = new RegisterRequest(
					"ADMIN_USER",
					"",
					"SenhaSegura@123");

			mockMvc.perform(post("/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(registerRequest)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus400WhenRegisteringUserWithEmailNull() throws Exception {

			RegisterRequest registerRequest = new RegisterRequest(
					"ADMIN_USER",
					null,
					"SenhaSegura@123");

			mockMvc.perform(post("/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsBytes(registerRequest)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus400WhenRegisteringUserWithEmptyPassword() throws Exception {

			RegisterRequest registerRequest = new RegisterRequest(
					"ADMIN_USER",
					"admin.user@email.com",
					"");

			mockMvc.perform(post("/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(registerRequest)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus400WhenRegisteringUserWithNullPassword() throws Exception {

			RegisterRequest registerRequest = new RegisterRequest(
					"ADMIN_USER",
					"admin.user@email.com",
					null);

			mockMvc.perform(post("/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(registerRequest)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus400WhenRegisteringUserWithPasswordWithoutCapitalLetter() throws Exception {

			RegisterRequest registerRequest = new RegisterRequest(
					"ADMIN_USER",
					"admin.user@email.com",
					"senhasegura@123");

			mockMvc.perform(post("/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(registerRequest)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus400WhenRegisteringUserWithPasswordWithoutLowerLetter() throws Exception {

			RegisterRequest registerRequest = new RegisterRequest(
					"ADMIN_USER",
					"admin.user@email.com",
					"SENHASEGURA@123");

			mockMvc.perform(post("/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(registerRequest)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus400WhenRegisteringUserWithPasswordWithoutNumbers() throws Exception {

			RegisterRequest registerRequest = new RegisterRequest(
					"ADMIN_USER",
					"admin.user@email.com",
					"SenhaSegura@@@@@");

			mockMvc.perform(post("/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(registerRequest)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus400WhenRegisteringUserWithPasswordWithoutSymbol() throws Exception {

			RegisterRequest registerRequest = new RegisterRequest(
					"ADMIN_USER",
					"admin.user@email.com",
					"SenhaSegura123456");

			mockMvc.perform(post("/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(registerRequest)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus400WhenRegisteringUserWithPasswordWithoutMinimumNumberOfCharactersReached()
				throws Exception {

			RegisterRequest registerRequest = new RegisterRequest(
					"ADMIN_USER",
					"admin.user@email.com",
					"SuaSenha@12");

			mockMvc.perform(post("/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(registerRequest)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus409WhenRegisteringUserWithAlreadyRegisteredEmail() throws Exception {
			createAdminUserForTest();

			RegisterRequest registerRequest = new RegisterRequest(
					"ADMIN_USER",
					"admin@gmail.com",
					"SuaSenha@12345");

			mockMvc.perform(post("/auth/register")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(registerRequest)))
					.andDo(print())
					.andExpect(status().isConflict());

		}

	}

	@Nested
	class loginAuthTest {

		@Test
		void shouldReturnStatus200whenLoginIsCompleted() throws Exception {
			createNormalUserForTest();

			AuthenticationRequest authenticationRequest = new AuthenticationRequest(
					"usertest@gmail.com",
					"@SenhaSegura123");

			mockMvc.perform(post("/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(authenticationRequest)))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.token").exists());
		}

		@Test
		void shouldReturnStatus400WhenLoginWithEmptyEmail() throws Exception {
			AuthenticationRequest authenticationRequest = new AuthenticationRequest(
					"",
					"@SenhaSegura123");

			mockMvc.perform(post("/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(authenticationRequest)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus400WhenLoginWithEmailNull() throws Exception {
			AuthenticationRequest authenticationRequest = new AuthenticationRequest(
					null,
					"@SenhaSegura123");

			mockMvc.perform(post("/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(authenticationRequest)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus400WhenLoginWithEmptyPassword() throws Exception {
			AuthenticationRequest authenticationRequest = new AuthenticationRequest(
					"usertest@gmail.com",
					"");

			mockMvc.perform(post("/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(authenticationRequest)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus400WhenLoginWithNullPassword() throws Exception {
			AuthenticationRequest authenticationRequest = new AuthenticationRequest(
					"usertest@gmail.com",
					null);

			mockMvc.perform(post("/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(authenticationRequest)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus401WhenLoginWithUnregisteredEmail() throws Exception {
			createNormalUserForTest();

			AuthenticationRequest authenticationRequest = new AuthenticationRequest(
					"emailInvalido@email.com",
					"@SenhaSegura123");

			mockMvc.perform(post("/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(authenticationRequest)))
					.andDo(print())
					.andExpect(status().isUnauthorized())
					.andExpect(jsonPath("$.status").value(401))
					.andExpect(jsonPath("$.error").value("Unauthorized"))
					.andExpect(jsonPath("$.message").value("Invalid credentials"))
					.andExpect(jsonPath("$.path").value("/auth/login"));
		}

		@Test
		void shouldReturnStatus401WhenLoginWithUnregisteredPassword() throws Exception {
			createNormalUserForTest();

			AuthenticationRequest authenticationRequest = new AuthenticationRequest(
					"usertest@gmail.com",
					"@SenhaSegura12345678");

			mockMvc.perform(post("/auth/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(authenticationRequest)))
					.andDo(print())
					.andExpect(status().isUnauthorized())
					.andExpect(jsonPath("$.status").value(401))
					.andExpect(jsonPath("$.error").value("Unauthorized"))
					.andExpect(jsonPath("$.message").value("Invalid credentials"))
					.andExpect(jsonPath("$.path").value("/auth/login"));
		}
	}
}
