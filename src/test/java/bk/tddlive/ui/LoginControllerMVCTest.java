package bk.tddlive.ui;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import bk.tddlive.security.AuthService;
import bk.tddlive.security.Authentication;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("src/main/webapp")
@ContextConfiguration(classes = LoginControllerMVCTest.TestConfig.class)
public class LoginControllerMVCTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void form() throws Exception {
        ResultActions result = mockMvc.perform(get("/login"));
        result.andDo(print());
        result.andExpect(status().isOk());
        result.andExpect(view().name(LoginController.FORM_VIEW));
    }

    @Test
    public void submit() throws Exception {
        mockMvc.perform(post("/login")
                .param("id", "user1")
                .param("password", "1234"))
                .andExpect(status().isOk())
                .andExpect(view().name(LoginController.SUCCESS_VIEW));
    }

    @Configuration
    @EnableWebMvc
    public static class TestConfig {
        @Bean
        public LoginController loginController() {
            LoginController loginController = new LoginController(authService());
            return loginController;
        }

        @Bean
        public AuthService authService() {
            AuthService authService = mock(AuthService.class);
            when(authService.authenticate("user1", "1234")).thenReturn(
                    new Authentication("user1"));
            return authService;
        }
    }
}
