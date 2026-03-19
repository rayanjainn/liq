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
 * Role-based access guard tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RoleGuardTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String S = "rg" + System.currentTimeMillis();

    private static String clientToken;
    private static String freelancerToken;
    
    @SuppressWarnings("unused")
    private static String adminToken;

    @Test @Order(1)
    @DisplayName("Setup — register client + freelancer + admin login")
    void setup() throws Exception {
        // Register CLIENT
        MvcResult cr = mvc.perform(multipart("/auth/register")
                        .param("name", "Guard Client").param("email", "gc_" + S + "@t.com")
                        .param("password", "P123!").param("role", "CLIENT"))
                .andExpect(status().isCreated()).andReturn();
        clientToken = objectMapper.readTree(cr.getResponse().getContentAsString()).at("/data/token").asText();

        // Register FREELANCER
        MvcResult fr = mvc.perform(multipart("/auth/register")
                        .param("name", "Guard Freelancer").param("email", "gf_" + S + "@t.com")
                        .param("password", "P123!").param("role", "FREELANCER"))
                .andExpect(status().isCreated()).andReturn();
        freelancerToken = objectMapper.readTree(fr.getResponse().getContentAsString()).at("/data/token").asText();

        // Login as ADMIN
        MvcResult ar = mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"admin@collabnex.com","password":"changeme"}
                                """))
                .andExpect(status().isOk()).andReturn();
        adminToken = objectMapper.readTree(ar.getResponse().getContentAsString()).at("/data/token").asText();
    }

    // ─── FREELANCER should NOT access CLIENT endpoints ────────

    @Test @Order(10)
    @DisplayName("Freelancer → POST /client/jobs → 403")
    void freelancerCannotPostJob() throws Exception {
        mvc.perform(post("/client/jobs")
                        .header("Authorization", "Bearer " + freelancerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Should Fail","description":"No access"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test @Order(11)
    @DisplayName("Freelancer → GET /client/jobs → 403")
    void freelancerCannotListClientJobs() throws Exception {
        mvc.perform(get("/client/jobs")
                        .header("Authorization", "Bearer " + freelancerToken))
                .andExpect(status().isForbidden());
    }

    // ─── CLIENT should NOT access FREELANCER endpoints ────────

    @Test @Order(20)
    @DisplayName("Client → GET /freelancer/jobs → 403")
    void clientCannotViewFreelancerJobs() throws Exception {
        mvc.perform(get("/freelancer/jobs")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
    }

    @Test @Order(21)
    @DisplayName("Client → POST /freelancer/jobs/999/apply → 403")
    void clientCannotApplyForJob() throws Exception {
        mvc.perform(post("/freelancer/jobs/999/apply")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
    }

    // ─── CLIENT should NOT access ADMIN endpoints ─────────────

    @Test @Order(30)
    @DisplayName("Client → GET /admin/jobs → 403")
    void clientCannotAccessAdmin() throws Exception {
        mvc.perform(get("/admin/jobs")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isForbidden());
    }

    // ─── FREELANCER should NOT access ADMIN endpoints ─────────

    @Test @Order(31)
    @DisplayName("Freelancer → GET /admin/jobs → 403")
    void freelancerCannotAccessAdmin() throws Exception {
        mvc.perform(get("/admin/jobs")
                        .header("Authorization", "Bearer " + freelancerToken))
                .andExpect(status().isForbidden());
    }

    @Test @Order(32)
    @DisplayName("Freelancer → POST /admin/jobs/999/shortlist → 403")
    void freelancerCannotShortlist() throws Exception {
        mvc.perform(post("/admin/jobs/999/shortlist")
                        .header("Authorization", "Bearer " + freelancerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"freelancerIds":[1,2,3]}
                                """))
                .andExpect(status().isForbidden());
    }

    // ─── No JWT → protected endpoints → 401/403 ──────────────

    @Test @Order(40)
    @DisplayName("No JWT → GET /client/jobs → 401 or 403")
    void noTokenClient() throws Exception {
        mvc.perform(get("/client/jobs"))
                .andExpect(status().is4xxClientError());
    }

    @Test @Order(41)
    @DisplayName("No JWT → GET /freelancer/jobs → 401 or 403")
    void noTokenFreelancer() throws Exception {
        mvc.perform(get("/freelancer/jobs"))
                .andExpect(status().is4xxClientError());
    }

    @Test @Order(42)
    @DisplayName("No JWT → GET /admin/jobs → 401 or 403")
    void noTokenAdmin() throws Exception {
        mvc.perform(get("/admin/jobs"))
                .andExpect(status().is4xxClientError());
    }

    // ─── Public endpoints still work without JWT ──────────────

    @Test @Order(50)
    @DisplayName("Public → POST /auth/login → accessible (no role needed)")
    void authEndpointPublic() throws Exception {
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"nonexistent@test.com","password":"test"}
                                """))
                // Should get 400/404 (bad credentials), NOT 403
                .andExpect(status().is4xxClientError())
                .andExpect(result -> {
                    int code = result.getResponse().getStatus();
                    Assertions.assertNotEquals(403, code,
                            "/auth/login should NOT return 403 — it's a public endpoint");
                });
    }
}
