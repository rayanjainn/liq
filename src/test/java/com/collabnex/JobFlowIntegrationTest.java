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
 * End-to-end integration test covering the full job lifecycle.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JobFlowIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String S = String.valueOf(System.currentTimeMillis());

    private static String clientToken;
    private static String adminToken;
    private static String[] freelancerTokens = new String[4];
    private static Long[] freelancerIds = new Long[4];
    private static Long job1Id;
    
    @SuppressWarnings("unused")
    private static Long job2Id;

    // ─── Phase 1: Setup users ─────────────────────────────────

    @Test @Order(1)
    @DisplayName("Phase 1.1 — Register CLIENT")
    void registerClient() throws Exception {
        MvcResult r = mvc.perform(multipart("/auth/register")
                        .param("name", "Flow Client")
                        .param("email", "flowclient_" + S + "@t.com")
                        .param("password", "Pass123!")
                        .param("role", "CLIENT"))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode j = objectMapper.readTree(r.getResponse().getContentAsString());
        clientToken = j.at("/data/token").asText();
    }

    @Test @Order(2)
    @DisplayName("Phase 1.2 — Register 4 FREELANCERS")
    void registerFreelancers() throws Exception {
        for (int i = 0; i < 4; i++) {
            MvcResult r = mvc.perform(multipart("/auth/register")
                            .param("name", "Freelancer " + i)
                            .param("email", "flowfl" + i + "_" + S + "@t.com")
                            .param("password", "Pass123!")
                            .param("role", "FREELANCER")
                            .param("phoneNumber", "55555" + i + "0000"))
                    .andExpect(status().isCreated())
                    .andReturn();
            JsonNode j = objectMapper.readTree(r.getResponse().getContentAsString());
            freelancerTokens[i] = j.at("/data/token").asText();
            freelancerIds[i] = j.at("/data/user/id").asLong();
        }
    }

    @Test @Order(3)
    @DisplayName("Phase 1.3 — Login as ADMIN")
    void loginAdmin() throws Exception {
        MvcResult r = mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"admin@collabnex.com","password":"changeme"}
                                """))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode j = objectMapper.readTree(r.getResponse().getContentAsString());
        adminToken = j.at("/data/token").asText();
    }

    // ─── Phase 2: Client posts jobs ───────────────────────────

    @Test @Order(10)
    @DisplayName("Phase 2.1 — Client creates Job #1")
    void clientCreatesJob1() throws Exception {
        MvcResult r = mvc.perform(post("/client/jobs")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Landing Page","description":"Build a React landing page"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Landing Page"))
                .andReturn();
        JsonNode j = objectMapper.readTree(r.getResponse().getContentAsString());
        job1Id = j.at("/data/id").asLong();
    }

    @Test @Order(11)
    @DisplayName("Phase 2.2 — Client creates Job #2")
    void clientCreatesJob2() throws Exception {
        MvcResult r = mvc.perform(post("/client/jobs")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"API Backend","description":"Spring Boot REST API"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode j = objectMapper.readTree(r.getResponse().getContentAsString());
        job2Id = j.at("/data/id").asLong();
    }

    @Test @Order(12)
    @DisplayName("Phase 2.3 — Client lists own jobs (2)")
    void clientListsJobs() throws Exception {
        mvc.perform(get("/client/jobs")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
    }

    // ─── Phase 3: Freelancers browse & apply ──────────────────

    @Test @Order(20)
    @DisplayName("Phase 3.1 — Freelancer views all jobs")
    void freelancerViewsJobs() throws Exception {
        mvc.perform(get("/freelancer/jobs")
                        .header("Authorization", "Bearer " + freelancerTokens[0]))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
    }

    @Test @Order(21)
    @DisplayName("Phase 3.2 — All 4 freelancers apply to Job #1")
    void freelancersApply() throws Exception {
        for (int i = 0; i < 4; i++) {
            mvc.perform(post("/freelancer/jobs/" + job1Id + "/apply")
                            .header("Authorization", "Bearer " + freelancerTokens[i]))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Test @Order(22)
    @DisplayName("Phase 3.3 — Duplicate application → 409")
    void duplicateApplication() throws Exception {
        mvc.perform(post("/freelancer/jobs/" + job1Id + "/apply")
                        .header("Authorization", "Bearer " + freelancerTokens[0]))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ─── Phase 4: Admin reviews & shortlists ──────────────────

    @Test @Order(30)
    @DisplayName("Phase 4.1 — Admin views all jobs")
    void adminViewsJobs() throws Exception {
        mvc.perform(get("/admin/jobs")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test @Order(31)
    @DisplayName("Phase 4.2 — Admin views applicants for Job #1 (4)")
    void adminViewsApplicants() throws Exception {
        mvc.perform(get("/admin/jobs/" + job1Id + "/applications")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(4));
    }

    @Test @Order(32)
    @DisplayName("Phase 4.3 — Shortlist with 2 IDs → 400 (too few)")
    void shortlistTooFew() throws Exception {
        String body = """
                {"freelancerIds":[%d,%d]}
                """.formatted(freelancerIds[0], freelancerIds[1]);

        mvc.perform(post("/admin/jobs/" + job1Id + "/shortlist")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test @Order(33)
    @DisplayName("Phase 4.4 — Shortlist 3 valid IDs → 200")
    void shortlistSuccess() throws Exception {
        String body = """
                {"freelancerIds":[%d,%d,%d]}
                """.formatted(freelancerIds[0], freelancerIds[1], freelancerIds[2]);

        mvc.perform(post("/admin/jobs/" + job1Id + "/shortlist")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ─── Phase 5: Client views shortlist ──────────────────────

    @Test @Order(40)
    @DisplayName("Phase 5.1 — Client views shortlisted candidates (3)")
    void clientViewsShortlist() throws Exception {
        mvc.perform(get("/client/jobs/" + job1Id + "/shortlisted")
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].name").isString())
                .andExpect(jsonPath("$.data[0].email").isString())
                .andExpect(jsonPath("$.data[0].phoneNumber").isString());
    }
}
