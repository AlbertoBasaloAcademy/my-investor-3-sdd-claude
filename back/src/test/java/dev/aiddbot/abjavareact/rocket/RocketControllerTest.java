package dev.aiddbot.abjavareact.rocket;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(RocketController.class)
class RocketControllerTest {

  @Autowired private MockMvc mvc;
  @Autowired private ObjectMapper mapper;
  @MockitoBean private RocketService service;

  private static final RocketResponse FALCON =
      new RocketResponse(
          1L, "Falcon 9", 9, "EARTH", "ACTIVE",
          LocalDate.of(2026, 1, 15), LocalDate.of(2026, 7, 15));

  private static final RocketRequest FALCON_REQUEST =
      new RocketRequest(
          "Falcon 9", 9, RocketRange.EARTH, RocketStatus.ACTIVE,
          LocalDate.of(2026, 1, 15), LocalDate.of(2026, 7, 15));

  @Test
  void findAllReturnsRocketList() throws Exception {
    given(service.findAll()).willReturn(List.of(FALCON));

    mvc.perform(get("/api/rockets"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("Falcon 9"))
        .andExpect(jsonPath("$[0].capacity").value(9))
        .andExpect(jsonPath("$[0].range").value("EARTH"))
        .andExpect(jsonPath("$[0].status").value("ACTIVE"));
  }

  @Test
  void findByIdReturnsRocket() throws Exception {
    given(service.findById(1L)).willReturn(FALCON);

    mvc.perform(get("/api/rockets/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Falcon 9"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  void findByIdReturns404WhenNotFound() throws Exception {
    given(service.findById(99L))
        .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mvc.perform(get("/api/rockets/99"))
        .andExpect(status().isNotFound());
  }

  @Test
  void createReturns201WithLocationHeader() throws Exception {
    given(service.create(any(RocketRequest.class))).willReturn(FALCON);

    mvc.perform(
            post("/api/rockets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(FALCON_REQUEST)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", endsWith("/api/rockets/1")))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Falcon 9"));
  }

  @Test
  void updateReturnsUpdatedRocket() throws Exception {
    RocketResponse updated =
        new RocketResponse(
            1L, "Falcon 9", 9, "MOON", "MAINTENANCE",
            LocalDate.of(2026, 6, 1), LocalDate.of(2026, 12, 1));
    given(service.update(eq(1L), any(RocketRequest.class))).willReturn(updated);

    mvc.perform(
            put("/api/rockets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(FALCON_REQUEST)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("MAINTENANCE"));
  }

  @Test
  void updateReturns404WhenNotFound() throws Exception {
    given(service.update(eq(99L), any(RocketRequest.class)))
        .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mvc.perform(
            put("/api/rockets/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(FALCON_REQUEST)))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteReturns204() throws Exception {
    willDoNothing().given(service).delete(1L);

    mvc.perform(delete("/api/rockets/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteReturns404WhenNotFound() throws Exception {
    willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).given(service).delete(99L);

    mvc.perform(delete("/api/rockets/99"))
        .andExpect(status().isNotFound());
  }
}
