package br.com.confidence.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

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
}
