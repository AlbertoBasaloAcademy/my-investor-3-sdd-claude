package dev.aiddbot.abjavareact.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import dev.aiddbot.abjavareact.launch.Launch;
import dev.aiddbot.abjavareact.launch.LaunchRepository;
import dev.aiddbot.abjavareact.launch.LaunchStatus;
import dev.aiddbot.abjavareact.rocket.Rocket;
import dev.aiddbot.abjavareact.rocket.RocketRange;
import dev.aiddbot.abjavareact.rocket.RocketStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

  @Mock private BookingRepository repository;
  @Mock private LaunchRepository launchRepository;
  @InjectMocks private BookingService service;

  private static final Rocket ROCKET =
      new Rocket("Falcon 9", 9, RocketRange.EARTH, RocketStatus.ACTIVE, null, null);
  private static final Launch LAUNCH =
      new Launch(ROCKET, LocalDate.now().plusMonths(3), new BigDecimal("50000"), LaunchStatus.CREATED);
  private static final BookingRequest REQUEST =
      new BookingRequest(1L, "Ada Lovelace", "ada@example.com", "555-0100");

  private Booking newBooking() {
    return new Booking(LAUNCH, "Ada Lovelace", "ada@example.com", "555-0100");
  }

  @Test
  void findAllReturnsMappedResponses() {
    given(repository.findAll()).willReturn(List.of(newBooking()));

    List<BookingResponse> result = service.findAll();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).status()).isEqualTo("CREATED");
    assertThat(result.get(0).launchRocketName()).isEqualTo("Falcon 9");
  }

  @Test
  void findByIdReturnsResponseWhenFound() {
    given(repository.findById(1L)).willReturn(Optional.of(newBooking()));

    BookingResponse result = service.findById(1L);

    assertThat(result.passengerName()).isEqualTo("Ada Lovelace");
    assertThat(result.passengerPhone()).isEqualTo("555-0100");
    assertThat(result.status()).isEqualTo("CREATED");
  }

  @Test
  void findByIdThrows404WhenNotFound() {
    given(repository.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.findById(99L))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("404");
  }

  @Test
  void createPersistsAndReturnsMappedResponse() {
    given(launchRepository.findById(1L)).willReturn(Optional.of(LAUNCH));
    given(repository.save(any())).willAnswer(inv -> inv.getArgument(0));

    BookingResponse result = service.create(REQUEST);

    assertThat(result.status()).isEqualTo("CREATED");
    assertThat(result.passengerName()).isEqualTo("Ada Lovelace");
    assertThat(result.passengerEmail()).isEqualTo("ada@example.com");
    assertThat(result.passengerPhone()).isEqualTo("555-0100");
  }

  @Test
  void createThrows400WhenLaunchIdIsNull() {
    BookingRequest bad = new BookingRequest(null, "Ada Lovelace", "ada@example.com", "555-0100");

    assertThatThrownBy(() -> service.create(bad))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("400");
  }

  @Test
  void createThrows400WhenPassengerNameIsBlank() {
    BookingRequest bad = new BookingRequest(1L, "  ", "ada@example.com", "555-0100");

    assertThatThrownBy(() -> service.create(bad))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("400");
  }

  @Test
  void createThrows400WhenPassengerEmailIsBlank() {
    BookingRequest bad = new BookingRequest(1L, "Ada Lovelace", "", "555-0100");

    assertThatThrownBy(() -> service.create(bad))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("400");
  }

  @Test
  void createThrows400WhenPassengerPhoneIsBlank() {
    BookingRequest bad = new BookingRequest(1L, "Ada Lovelace", "ada@example.com", "");

    assertThatThrownBy(() -> service.create(bad))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("400");
  }

  @Test
  void createThrows400WhenLaunchNotFound() {
    given(launchRepository.findById(1L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.create(REQUEST))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("400");
  }

  @Test
  void cancelSetsStatusAndPersists() {
    given(repository.findById(1L)).willReturn(Optional.of(newBooking()));
    given(repository.save(any())).willAnswer(inv -> inv.getArgument(0));

    BookingResponse result = service.cancel(1L);

    assertThat(result.status()).isEqualTo("CANCELLED");
  }

  @Test
  void cancelThrows404WhenNotFound() {
    given(repository.findById(99L)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.cancel(99L))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("404");
  }
}
