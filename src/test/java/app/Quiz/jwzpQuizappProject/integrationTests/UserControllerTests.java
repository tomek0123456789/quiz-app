package app.Quiz.jwzpQuizappProject.integrationTests;

import app.Quiz.jwzpQuizappProject.controllers.UserController;
import app.Quiz.jwzpQuizappProject.models.users.UserDto;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.service.TokenService;
import app.Quiz.jwzpQuizappProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static app.Quiz.jwzpQuizappProject.integrationTests.IntTestsHelper.asJsonString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = TestConfig.class)
@Import(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserService userService;

    private String token = "Bearer token";

    String user1Name = "user 1 name";
    String user2Name = "user 2 name";

    @Test
    @WithMockUser
    public void testGetMultipleUsers() throws Exception {
        UserModel user1 = new UserModel();
        user1.setId(1);
        user1.setName(user1Name);
        UserModel user2 = new UserModel();
        user2.setId(2);
        user2.setName(user2Name);

        var users = List.of(user1, user2);
        when(userService.getMultipleUsers(any(Optional.class))).thenReturn(users);

        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value(user1Name))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value(user2Name));

        verify(userService, times(1)).getMultipleUsers(any(Optional.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    @WithMockUser
    public void testGetSingleUser() throws Exception {
        // Given
        UserModel user1 = new UserModel();
        user1.setId(1);
        user1.setName(user1Name);
        when(userService.getUserById(1)).thenReturn(user1);

        // When/Then
        mockMvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(user1Name));

        verify(userService, times(1)).getUserById(1);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @WithMockUser
    public void testGetMe() throws Exception {
        UserModel user1 = new UserModel();
        user1.setId(1);
        user1.setName(user1Name);
        when(tokenService.getUserFromToken(anyString())).thenReturn(user1);

        mockMvc.perform(get("/users/me")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(user1Name));

        verify(tokenService, times(1)).getUserFromToken(token);
        verifyNoMoreInteractions(tokenService);
    }

    @Test
    @WithMockUser
    public void testDeactivateUser() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deactivateUser(1, token);
        verifyNoMoreInteractions(userService);
    }

    @Test
    @WithMockUser
    public void testUpdateUser() throws Exception {
        UserDto userDto = new UserDto(1, "email", "password");

        mockMvc.perform(put("/users")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).updateUser(eq(userDto), eq(token));
        verifyNoMoreInteractions(userService);
    }
}
