package br.com.confidence.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import br.com.confidence.dto.authentication.AuthenticationRequest;
import br.com.confidence.dto.authentication.ForgotPasswordRequestDTO;
import br.com.confidence.dto.authentication.RegisterRequest;
import br.com.confidence.dto.authentication.ResetPasswordRequestDTO;
import br.com.confidence.model.auth.PasswordResetToken;
import br.com.confidence.model.user.User;
import br.com.confidence.service.email.EmailService;

public class AuthControllerIT extends BaseIntegrationTests {

	@MockBean
	private EmailService emailService;

	@Captor
	private ArgumentCaptor<String> toCaptor;

	@Captor
	private ArgumentCaptor<String> subjectCaptor;

	@Captor
	private ArgumentCaptor<String> bodyCaptor;

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

	@Nested
	class forgotPasswordTest {

		@Test
		void shouldReturnStatus204WhenCreatingRedefinedTokenAndSendingEmail() throws Exception {
			// Arrange
			createNormalUserForTest();

			ForgotPasswordRequestDTO dto = new ForgotPasswordRequestDTO("usertest@gmail.com");

			ArgumentCaptor<String> toCaptor = ArgumentCaptor.forClass(String.class);
			ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
			ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

			// Act + Assert (HTTP)
			mockMvc.perform(post("/auth/forgot-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(dto)))
					.andDo(print())
					.andExpect(status().isNoContent());

			// Assert (email sent)
			verify(emailService, times(1))
					.sendSimpleMail(toCaptor.capture(), subjectCaptor.capture(), bodyCaptor.capture());

			assertEquals("usertest@gmail.com", toCaptor.getValue());
			assertEquals("Password reset request", subjectCaptor.getValue());

			String body = bodyCaptor.getValue();
			assertNotNull(body);
			assertTrue(body.contains("token="), "Email body should contain token param");

			// Extract token from body
			Pattern p = Pattern.compile("token=([\\w-]+)");
			Matcher m = p.matcher(body);
			assertTrue(m.find(), "Email body should contain token value");
			String tokenValue = m.group(1);

			assertNotNull(tokenValue);
			assertFalse(tokenValue.isBlank());

			// Assert (persisted token + associated with the correct user + expiration)
			PasswordResetToken token = passwordResetTokenRepository.findByTokenWithUser(tokenValue)
					.orElseThrow(() -> new AssertionError("Token should be persisted"));

			assertEquals("usertest@gmail.com", token.getUser().getEmail());
			assertEquals(tokenValue, token.getToken());
			assertEquals("usertest@gmail.com", token.getUser().getEmail());
			assertTrue(token.getExpiryDate().isAfter(LocalDateTime.now()),
					"Token expiryDate should be in the future");
		}

		@Test
		void shouldReturnStatus204WhenEmailNotRegisteredInAvailableField() throws Exception {
			createNormalUserForTest();

			ForgotPasswordRequestDTO requestDTO = new ForgotPasswordRequestDTO("falseemail@email.com");

			mockMvc.perform(post("/auth/forgot-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDTO)))
					.andDo(print())
					.andExpect(status().isNoContent());

			verify(emailService, never()).sendSimpleMail(anyString(), anyString(), anyString());
		}

		@Test
		void shouldReturnStatus400WhenInformedEmailWithInvalidFormatInAvailableField() throws Exception {
			createNormalUserForTest();

			ForgotPasswordRequestDTO requestDTO = new ForgotPasswordRequestDTO("usertest_gmail.com");

			mockMvc.perform(post("/auth/forgot-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDTO)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus400WhenInformedEmailWithSizeSmallerThanMinimumAcceptedInAvailableField()
				throws Exception {
			createNormalUserForTest();

			ForgotPasswordRequestDTO requestDTO = new ForgotPasswordRequestDTO("user@email.com");

			mockMvc.perform(post("/auth/forgot-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDTO)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}
	}

	@Nested
	class resetPasswordTest {

		@Test
		void shouldReturnStatus204WhenChangePasswordWithValidDataAndToken() throws Exception {
			String token = createValidPasswordResetToken();
			String newPassword = "NewPassword@123";

			ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(token, newPassword);

			mockMvc.perform(post("/auth/reset-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDTO)))
					.andDo(print())
					.andExpect(status().isNoContent());

			assertTrue(passwordResetTokenRepository.findByToken(token).isEmpty());

			User updated = userRepository.findByEmail("usertest@gmail.com").orElseThrow();
			assertTrue(passwordEncoder.matches(newPassword, updated.getPassword()));

		}

		@Test
		void mustReturnStatus400WhenPerformingPasswordChangeWithTokenNull() throws Exception {
			String token = null;
			String newPassword = "NewPassword@123";

			ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(token, newPassword);

			mockMvc.perform(post("/auth/reset-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDTO)))
					.andDo(print())
					.andExpect(status().isBadRequest());

		}

		@Test
		void mustReturnStatus400WhenPerformingPasswordChangeWithEmptyToken() throws Exception {
			String token = "";
			String newPassword = "NewPassword@123";

			ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(token, newPassword);

			mockMvc.perform(post("/auth/reset-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDTO)))
					.andDo(print())
					.andExpect(status().isBadRequest());

		}

		@Test
		void mustReturnStatus400WhenChangingPasswordWithInvalidToken() throws Exception {
			String token = "InvalidPasswordResetToken";
			String newPassword = "NewPassword@123";

			ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(token, newPassword);

			mockMvc.perform(post("/auth/reset-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDTO)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus400WhenChangePasswordWithExpiredToken() throws Exception {
			User user = createNormalUserForTest();

			String tokenValue = UUID.randomUUID().toString();
			String newPassword = "NewPassword@123";

			PasswordResetToken token = new PasswordResetToken();
			token.setToken(tokenValue);
			token.setUser(user);
			token.setExpiryDate(LocalDateTime.now().minusHours(1));

			passwordResetTokenRepository.save(token);

			ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(tokenValue, newPassword);

			mockMvc.perform(post("/auth/reset-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDTO)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus400WhenPasswordChangeWithTokenUsed() throws Exception {
			String tokenValue = createValidPasswordResetToken();
			String newPassword = "NewPassword@123";

			ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(tokenValue, newPassword);

			mockMvc.perform(post("/auth/reset-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDTO)))
					.andDo(print())
					.andExpect(status().isNoContent());

			User afterFirst = userRepository.findByEmail("usertest@gmail.com").orElseThrow();
			String encodedAfterFirst = afterFirst.getPassword();
			assertTrue(passwordEncoder.matches(newPassword, encodedAfterFirst));

			mockMvc.perform(post("/auth/reset-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDTO)))
					.andDo(print())
					.andExpect(status().isBadRequest());

			assertTrue(passwordResetTokenRepository.findByToken(tokenValue).isEmpty());

			User afterSecond = userRepository.findByEmail("usertest@gmail.com").orElseThrow();
			assertEquals(encodedAfterFirst, afterSecond.getPassword());
		}

		@Test
		void mustReturnStatus400WhenPerformingPasswordChangeWithPasswordNull() throws Exception {
			String tokenValue = createValidPasswordResetToken();
			String newPassword = null;

			ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(tokenValue, newPassword);

			mockMvc.perform(post("/auth/reset-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDTO)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void mustReturnStatus400WhenPerformingPasswordChangeWithEmptyPassword() throws Exception {
			String tokenValue = createValidPasswordResetToken();
			String newPassword = "";

			ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(tokenValue, newPassword);

			mockMvc.perform(post("/auth/reset-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDTO)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void mustReturnStatus400WhenChangingPasswordWithMinimumLettersMinor() throws Exception {
			String tokenValue = createValidPasswordResetToken();
			String newPassword = "@Aa45678911";

			ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(tokenValue, newPassword);

			mockMvc.perform(post("/auth/reset-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDTO)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void mustReturnStatus400WhenChangingPasswordWithPasswordWithoutCapitalLetter() throws Exception {
			String tokenValue = createValidPasswordResetToken();
			String newPassword = "@novasenhasegura123";

			ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(tokenValue, newPassword);

			mockMvc.perform(post("/auth/reset-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDTO)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void mustReturnStatus400WhenChangingPasswordWithPasswordWithoutLowerLetter() throws Exception {
			String tokenValue = createValidPasswordResetToken();
			String newPassword = "@NOVASENHASEGURA123";

			ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(tokenValue, newPassword);

			mockMvc.perform(post("/auth/reset-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDTO)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void shouldReturnStatus400WhenChangePasswordWithPasswordWithoutNumbers() throws Exception {
			String tokenValue = createValidPasswordResetToken();
			String newPassword = "@NovaSenhaSegura";

			ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(tokenValue, newPassword);

			mockMvc.perform(post("/auth/reset-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDTO)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}

		@Test
		void mustReturnStatus400WhenChangingPasswordWithPasswordWithoutSpecialCharacter() throws Exception {
			String tokenValue = createValidPasswordResetToken();
			String newPassword = "NovaSenhaSegura123";

			ResetPasswordRequestDTO requestDTO = new ResetPasswordRequestDTO(tokenValue, newPassword);

			mockMvc.perform(post("/auth/reset-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDTO)))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}
	}

	@Nested
	class userInformationTest {
		@Test
		@WithMockUser(username = "admin@confidence.com", roles = { "USER", "ADMIN" })
		void shouldReturnStatus200WhenSearchingForUserInformationWithValidData() throws Exception {
			mockMvc.perform(get("/auth/me"))
					.andDo(print())
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.email").value("admin@confidence.com"))
					.andExpect(jsonPath("$.roles").isArray());
		}

		@Test
		void shouldReturnStatus401WhenSearchingUserInformationWithoutAuthentication() throws Exception {
			mockMvc.perform(get("/auth/me"))
					.andDo(print())
					.andExpect(status().isUnauthorized());
		}

		@Test
		@WithMockUser(username = "user@confidence.com", roles = { "GUEST" })
		void shouldReturn403WhenAuthenticatedButWithoutRequiredRole() throws Exception {
			mockMvc.perform(get("/auth/me"))
					.andExpect(status().isForbidden());
		}
	}
}
