package dev.aiddbot.abjavareact.booking;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

@WebMvcTest(BookingController.class)
class BookingControllerTest {

  @Autowired private MockMvc mvc;
  @Autowired private ObjectMapper mapper;
  @MockitoBean private BookingService service;

  private static final LocalDate DATE = LocalDate.of(2027, 6, 15);

  private static final BookingResponse APOLLO =
      new BookingResponse(
          1L, 2L, "Falcon 9", DATE, "Ada Lovelace", "ada@example.com", "555-0100", "CREATED");

  private static final BookingRequest APOLLO_REQUEST =
      new BookingRequest(2L, "Ada Lovelace", "ada@example.com", "555-0100");

  @Test
  void findAllReturnsBookingList() throws Exception {
    given(service.findAll()).willReturn(List.of(APOLLO));

    mvc.perform(get("/api/bookings"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].passengerName").value("Ada Lovelace"))
        .andExpect(jsonPath("$[0].status").value("CREATED"));
  }

  @Test
  void findByIdReturnsBooking() throws Exception {
    given(service.findById(1L)).willReturn(APOLLO);

    mvc.perform(get("/api/bookings/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.passengerName").value("Ada Lovelace"))
        .andExpect(jsonPath("$.passengerPhone").value("555-0100"))
        .andExpect(jsonPath("$.status").value("CREATED"));
  }

  @Test
  void findByIdReturns404WhenNotFound() throws Exception {
    given(service.findById(99L)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mvc.perform(get("/api/bookings/99")).andExpect(status().isNotFound());
  }

  @Test
  void createReturns201WithLocationHeader() throws Exception {
    given(service.create(any(BookingRequest.class))).willReturn(APOLLO);

    mvc.perform(
            post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(APOLLO_REQUEST)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", endsWith("/api/bookings/1")))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.passengerName").value("Ada Lovelace"));
  }

  @Test
  void createReturns400WhenMissingPassengerData() throws Exception {
    given(service.create(any(BookingRequest.class)))
        .willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passenger name is required"));

    mvc.perform(
            post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(new BookingRequest(2L, "", "", ""))))
        .andExpect(status().isBadRequest());
  }

  @Test
  void cancelReturnsCancelledBooking() throws Exception {
    BookingResponse cancelled =
        new BookingResponse(
            1L, 2L, "Falcon 9", DATE, "Ada Lovelace", "ada@example.com", "555-0100", "CANCELLED");
    given(service.cancel(1L)).willReturn(cancelled);

    mvc.perform(post("/api/bookings/1/cancel"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CANCELLED"));
  }

  @Test
  void cancelReturns404WhenNotFound() throws Exception {
    given(service.cancel(eq(99L))).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mvc.perform(post("/api/bookings/99/cancel")).andExpect(status().isNotFound());
  }
}
