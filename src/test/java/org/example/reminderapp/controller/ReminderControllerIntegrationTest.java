package org.example.reminderapp.controller;

import org.example.reminderapp.config.TestBotConfig;
import org.example.reminderapp.dto.LoginRequest;
import org.example.reminderapp.dto.ReminderDto;
import org.example.reminderapp.dto.UserDto;
import org.example.reminderapp.service.TelegramService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
@Import(TestBotConfig.class)
public class ReminderControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TelegramService telegramService;

    @MockitoBean
    private JavaMailSender mailSender;

    private String jwtToken;
    private String testEmail;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testEmail = "test" + System.currentTimeMillis() + "@mail.com";

        createUser(testEmail, "password");
        jwtToken = login(testEmail, "password");
    }

    private UserDto createUser(String email, String password) throws Exception {
        Map<String, Object> request = Map.of(
                "email", email,
                "password", password,
                "telegramChatId", "12345"
        );

        MvcResult result = mockMvc.perform(post("/api/v1/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
    }

    private String login(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        return com.jayway.jsonpath.JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }

    @Test
    void createReminder_ShouldReturn201_WhenValidRequest() throws Exception {
        ReminderDto request = ReminderDto.builder()
                .title("Test reminder")
                .description("Test description")
                .remind(LocalDateTime.now().plusDays(1))
                .build();

        mockMvc.perform(post("/api/v1/reminder/create")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void getRemindersList_ShouldReturn200_WithPagination() throws Exception {
        mockMvc.perform(get("/api/v1/reminder/list")
                .header("Authorization", "Bearer " + jwtToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));

    }

    @Test
    void sortReminders_ShouldReturn200_WhenSortByName() throws Exception {
        mockMvc.perform(get("/api/v1/reminder/sort")
                .header("Authorization", "Bearer " + jwtToken)
                .param("by", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void filterReminders_ShouldReturn200_WhenFilterByDate() throws Exception {
        mockMvc.perform(get("/api/v1/reminder/filtr")
                .header("Authorization", "Bearer " + jwtToken)
                .param("date", "2026-03-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getReminderById_ShouldReturn404_WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/reminder/999")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }
}
