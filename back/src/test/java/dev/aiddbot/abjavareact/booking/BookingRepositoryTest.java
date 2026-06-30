package dev.aiddbot.abjavareact.booking;

import static org.assertj.core.api.Assertions.assertThat;

import dev.aiddbot.abjavareact.launch.Launch;
import dev.aiddbot.abjavareact.launch.LaunchRepository;
import dev.aiddbot.abjavareact.launch.LaunchStatus;
import dev.aiddbot.abjavareact.rocket.Rocket;
import dev.aiddbot.abjavareact.rocket.RocketRange;
import dev.aiddbot.abjavareact.rocket.RocketRepository;
import dev.aiddbot.abjavareact.rocket.RocketStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class BookingRepositoryTest {

  @Autowired private BookingRepository repository;
  @Autowired private LaunchRepository launchRepository;
  @Autowired private RocketRepository rocketRepository;

  private Launch savedLaunch() {
    Rocket rocket =
        rocketRepository.save(
            new Rocket("Falcon 9", 9, RocketRange.EARTH, RocketStatus.ACTIVE, null, null));
    return launchRepository.save(
        new Launch(rocket, LocalDate.now().plusMonths(1), new BigDecimal("50000"), LaunchStatus.CREATED));
  }

  @Test
  void persistsAndReadsBackBooking() {
    Launch launch = savedLaunch();
    Booking booking = new Booking(launch, "Ada Lovelace", "ada@example.com", "555-0100");

    Booking saved = repository.save(booking);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getLaunch().getId()).isEqualTo(launch.getId());
    assertThat(saved.getPassengerName()).isEqualTo("Ada Lovelace");
    assertThat(saved.getPassengerEmail()).isEqualTo("ada@example.com");
    assertThat(saved.getPassengerPhone()).isEqualTo("555-0100");
    assertThat(saved.getStatus()).isEqualTo(BookingStatus.CREATED);
  }

  @Test
  void findsAllBookings() {
    Launch launch = savedLaunch();
    repository.save(new Booking(launch, "Ada Lovelace", "ada@example.com", "555-0100"));
    repository.save(new Booking(launch, "Grace Hopper", "grace@example.com", "555-0200"));

    assertThat(repository.findAll()).hasSize(2);
  }

  @Test
  void persistsCancelledBooking() {
    Launch launch = savedLaunch();
    Booking booking = new Booking(launch, "Ada Lovelace", "ada@example.com", "555-0100");
    booking.setStatus(BookingStatus.CANCELLED);

    Booking saved = repository.save(booking);

    assertThat(saved.getStatus()).isEqualTo(BookingStatus.CANCELLED);
  }
}
