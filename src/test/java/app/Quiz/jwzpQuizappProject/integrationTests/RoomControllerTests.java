package app.Quiz.jwzpQuizappProject.integrationTests;

import app.Quiz.jwzpQuizappProject.controllers.RoomController;
import app.Quiz.jwzpQuizappProject.models.results.QuizResultsModel;
import app.Quiz.jwzpQuizappProject.models.results.ResultsDto;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomDto;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomModel;
import app.Quiz.jwzpQuizappProject.models.users.UserModel;
import app.Quiz.jwzpQuizappProject.service.RoomService;
import app.Quiz.jwzpQuizappProject.service.TokenService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;

import static app.Quiz.jwzpQuizappProject.integrationTests.IntTestsHelper.asJsonString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
@ContextConfiguration(classes = TestConfig.class)
@Import(RoomController.class)
public class RoomControllerTests {
    @Mock
    private RoomService roomService;
    @Mock
    private TokenService tokenService;

    @Autowired
    private MockMvc mockMvc;
    private String token = "Bearer token";
    @Test
    @WithMockUser
    public void testGetSingleRoom_ValidRoomId_ShouldReturnRoomModel() throws Exception {
        long roomId = 1;
        RoomModel roomModel = new RoomModel();
        roomModel.setId(roomId);

        when(roomService.getSingleRoom(eq(roomId), eq(token))).thenReturn(roomModel);

        mockMvc.perform(MockMvcRequestBuilders.get("/myrooms/{roomId}", roomId)
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void testCreateRoom_ValidRequestBody_ShouldReturnCreatedStatus() throws Exception {
        var startTime = Instant.parse("2018-04-29T10:15:30.00Z");
        RoomDto roomDto = new RoomDto("room name", startTime, startTime);
        RoomModel roomModel = new RoomModel();

        when(roomService.createRoom(any(RoomDto.class), anyString())).thenReturn(roomModel);
        when(tokenService.getEmailFromToken(anyString())).thenReturn("test@test.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/myrooms")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(roomDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void testDeleteRoom_ValidRoomId_ShouldReturnNoContentStatus() throws Exception {
        long roomId = 1;

        mockMvc.perform(MockMvcRequestBuilders.delete("/myrooms/{roomId}", roomId)
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testAddParticipantToRoom() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/myrooms/1/users/2")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testRemoveParticipantFromRoom() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/myrooms/1/users/2")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetRoomResults() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/myrooms/1/results")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreateResults() throws Exception {

        ResultsDto resultsDto = new ResultsDto(new HashSet<QuizResultsModel>(), new UserModel(), LocalDateTime.parse("2018-04-29T10:15:30.00"), 0);

        mockMvc.perform(MockMvcRequestBuilders.post("/myrooms/1/results")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(resultsDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser()
    public void testGetAllRooms() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/myrooms")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser()
    public void testAddQuizToRoom() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/myrooms/1/quizzes/2")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testRemoveQuizFromRoom() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/myrooms/1/quizzes/2")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }


}
