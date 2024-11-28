package com.gd.ecom.controller;

import com.gd.ecom.model.UserDto;
import com.gd.ecom.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountServiceImpl accountService;

    @Test
    @DisplayName("Register User - Success")
    void testRegisterUser_Success() throws Exception {
        when(accountService.addUser(any(UserDto.class))).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"StrongPassword123!\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(accountService, times(1)).addUser(any(UserDto.class));
    }

    @Test
    @DisplayName("Register User - User Already Exists")
    void testRegisterUser_UserAlreadyExists() throws Exception {
        when(accountService.addUser(any(UserDto.class))).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"StrongPassword123!\"}"))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        verify(accountService, times(1)).addUser(any(UserDto.class));
    }

    @Test
    @DisplayName("Register User - Invalid User Data")
    void testRegisterUser_InvalidUserDto() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"invalid-email\",\"password\":\"weak\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.password", hasSize(greaterThan(1))));

        verify(accountService, never()).addUser(any(UserDto.class));
    }

    @Test
    @DisplayName("Authenticate User - Success")
    void testAuthenticate_Success() throws Exception {
        when(accountService.authenticate("test@example.com", "StrongPassword123!")).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/accounts/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"StrongPassword123!\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.isAuthenticated").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sessionId").exists());

        verify(accountService, times(1)).authenticate("test@example.com", "StrongPassword123!");
    }

    @Test
    @DisplayName("Authenticate User - Invalid Login")
    void testAuthenticate_InvalidLogin() throws Exception {
        when(accountService.authenticate("test@example.com", "wrongpassword")).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/accounts/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    if (!content.contains("Invalid username or password")) {
                        throw new AssertionError("Expected content 'Invalid username or password' but was '" + content + "'");
                    }
                });

        verify(accountService, times(1)).authenticate("test@example.com", "wrongpassword");
    }
}
