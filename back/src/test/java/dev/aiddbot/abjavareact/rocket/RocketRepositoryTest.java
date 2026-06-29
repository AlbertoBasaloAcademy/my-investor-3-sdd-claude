package dev.aiddbot.abjavareact.rocket;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class RocketRepositoryTest {

  @Autowired private RocketRepository repository;

  @Test
  void persistsAndReadsBackRocket() {
    Rocket rocket =
        new Rocket(
            "Falcon 9",
            9,
            RocketRange.EARTH,
            RocketStatus.ACTIVE,
            LocalDate.of(2026, 1, 15),
            LocalDate.of(2026, 7, 15));

    Rocket saved = repository.save(rocket);

    assertThat(saved.getId()).isNotNull();
    assertThat(repository.findById(saved.getId()))
        .isPresent()
        .get()
        .satisfies(
            found -> {
              assertThat(found.getName()).isEqualTo("Falcon 9");
              assertThat(found.getCapacity()).isEqualTo(9);
              assertThat(found.getRange()).isEqualTo(RocketRange.EARTH);
              assertThat(found.getStatus()).isEqualTo(RocketStatus.ACTIVE);
              assertThat(found.getLastMaintenanceDate()).isEqualTo(LocalDate.of(2026, 1, 15));
              assertThat(found.getNextMaintenanceDate()).isEqualTo(LocalDate.of(2026, 7, 15));
            });
  }

  @Test
  void findsAllRockets() {
    repository.save(new Rocket("Falcon 9", 9, RocketRange.EARTH, RocketStatus.ACTIVE, null, null));
    repository.save(new Rocket("Starship", 100, RocketRange.MARS, RocketStatus.MAINTENANCE, null, null));

    assertThat(repository.findAll()).hasSize(2);
  }

  @Test
  void deletesRocket() {
    Rocket saved =
        repository.save(new Rocket("Falcon 9", 9, RocketRange.EARTH, RocketStatus.ACTIVE, null, null));

    repository.deleteById(saved.getId());

    assertThat(repository.findById(saved.getId())).isEmpty();
  }
}
