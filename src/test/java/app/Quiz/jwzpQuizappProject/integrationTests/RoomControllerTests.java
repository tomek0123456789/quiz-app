package app.Quiz.jwzpQuizappProject.integrationTests;

import app.Quiz.jwzpQuizappProject.controllers.RoomController;
import app.Quiz.jwzpQuizappProject.models.rooms.RoomModel;
import app.Quiz.jwzpQuizappProject.service.RoomService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
@ContextConfiguration(classes = TestConfig.class)
@Import(RoomController.class)
public class RoomControllerTests {
    @Mock
    private RoomService roomService;

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

//        verify(roomService).getSingleRoom(eq(roomId), eq(token));
    }

//    @Test
//    @WithMockUser
//    public void testCreateRoom_ValidRequestBody_ShouldReturnCreatedStatus() throws Exception {
//        var startTime = Instant.parse("2018-04-29T10:15:30.00Z");
//        RoomDto roomDto = new RoomDto("room name", startTime, startTime);
//        RoomModel roomModel = new RoomModel();
//
//        when(roomService.createRoom(roomDto, token)).thenReturn(roomModel);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/myrooms")
//                        .with(csrf())
//                        .header(HttpHeaders.AUTHORIZATION, token)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(roomDto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").value(roomModel.getId()));
//
//        verify(roomService).createRoom(roomDto, token);
//    }
//
//
//
//    @Test
//    @WithMockUser
//    public void testDeleteRoom_ValidRoomId_ShouldReturnNoContentStatus() throws Exception {
//        long roomId = 1;
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/myrooms/{roomId}", roomId)
//                        .with(csrf())
//                        .header(HttpHeaders.AUTHORIZATION, token))
//                .andExpect(status().isNoContent());
//
//        verify(roomService).deleteRoom(roomId, token);
//    }


}
