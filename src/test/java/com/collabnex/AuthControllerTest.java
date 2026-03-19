package com.collabnex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the Auth endpoints: /auth/register and /auth/login.
 *
 * Uses a unique email suffix per test run to avoid collisions with existing data.
 *
 * <p>Test order:
 * <ol>
 *   <li>Register a CLIENT</li>
 *   <li>Register a FREELANCER</li>
 *   <li>Block ADMIN self-registration</li>
 *   <li>Reject duplicate email</li>
 *   <li>Login as CLIENT</li>
 *   <li>Login as ADMIN (env creds)</li>
 *   <li>Reject wrong password</li>
 * </ol>
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Unique suffix to avoid email collisions across test runs
    private static final String SUFFIX = String.valueOf(System.currentTimeMillis());
    
    private static String clientToken;
    
    private static String freelancerToken;

    @Test
    @Order(1)
    @DisplayName("01 - Register CLIENT → 201 + token returned")
    void registerClient() throws Exception {
        MvcResult result = mvc.perform(multipart("/auth/register")
                        .param("name", "Test Client")
                        .param("email", "client_" + SUFFIX + "@test.com")
                        .param("password", "Password123!")
                        .param("role", "CLIENT")
                        .param("phoneNumber", "9876543210"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isString())
                .andExpect(jsonPath("$.data.user.role").value("CLIENT"))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        clientToken = json.at("/data/token").asText();
    }

    @Test
    @Order(2)
    @DisplayName("02 - Register FREELANCER → 201 + token returned")
    void registerFreelancer() throws Exception {
        MvcResult result = mvc.perform(multipart("/auth/register")
                        .param("name", "Test Freelancer")
                        .param("email", "freelancer_" + SUFFIX + "@test.com")
                        .param("password", "Password123!")
                        .param("role", "FREELANCER")
                        .param("phoneNumber", "1111111111"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.user.role").value("FREELANCER"))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        freelancerToken = json.at("/data/token").asText();
    }

    @Test
    @Order(3)
    @DisplayName("03 - Register ADMIN → blocked (400)")
    void registerAdminBlocked() throws Exception {
        mvc.perform(multipart("/auth/register")
                        .param("name", "Hacker")
                        .param("email", "hacker_" + SUFFIX + "@test.com")
                        .param("password", "Password123!")
                        .param("role", "ADMIN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @Order(4)
    @DisplayName("04 - Duplicate email registration → 409")
    void registerDuplicateEmail() throws Exception {
        mvc.perform(multipart("/auth/register")
                        .param("name", "Dup Client")
                        .param("email", "client_" + SUFFIX + "@test.com")
                        .param("password", "Password123!")
                        .param("role", "CLIENT"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @Order(5)
    @DisplayName("05 - Login as CLIENT → 200 + correct role")
    void loginClient() throws Exception {
        String body = """
                {"email":"client_%s@test.com","password":"Password123!"}
                """.formatted(SUFFIX);

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.role").value("CLIENT"));
    }

    @Test
    @Order(6)
    @DisplayName("06 - Login as ADMIN (env creds) → 200 + uid=-1")
    void loginAdmin() throws Exception {
        String body = """
                {"email":"admin@collabnex.com","password":"changeme"}
                """;

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.role").value("ADMIN"))
                .andExpect(jsonPath("$.data.user.id").value(-1));
    }

    @Test
    @Order(7)
    @DisplayName("07 - Login with wrong password → 400")
    void loginWrongPassword() throws Exception {
        String body = """
                {"email":"client_%s@test.com","password":"WrongPassword"}
                """.formatted(SUFFIX);

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
