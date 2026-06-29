package dev.aiddbot.abjavareact.rocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class RocketServiceTest {

  @Mock private RocketRepository repository;
  @InjectMocks private RocketService service;

  private static final LocalDate LAST = LocalDate.of(2026, 1, 15);
  private static final LocalDate NEXT = LocalDate.of(2026, 7, 15);

  private Rocket savedRocket(Long id) {
    return new Rocket("Falcon 9", 9, RocketRange.EARTH, RocketStatus.ACTIVE, LAST, NEXT);
  }

  @Test
  void findAllReturnsMappedResponses() {
    Rocket rocket = new Rocket("Falcon 9", 9, RocketRange.EARTH, RocketStatus.ACTIVE, LAST, NEXT);
    given(repository.findAll()).willReturn(List.of(rocket));

    List<RocketResponse> result = service.findAll();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).name()).isEqualTo("Falcon 9");
    assertThat(result.get(0).status()).isEqualTo("ACTIVE");
  }

  @Test
  void findByIdReturnsResponseWhenFound() {
    Rocket rocket = new Rocket("Falcon 9", 9, RocketRange.EARTH, RocketStatus.ACTIVE, LAST, NEXT);
    given(repository.findById(1L)).willReturn(Optional.of(rocket));

    RocketResponse result = service.findById(1L);

    assertThat(result.name()).isEqualTo("Falcon 9");
    assertThat(result.capacity()).isEqualTo(9);
    assertThat(result.range()).isEqualTo("EARTH");
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
    RocketRequest request = new RocketRequest("Falcon 9", 9, RocketRange.EARTH, RocketStatus.ACTIVE, LAST, NEXT);
    Rocket saved = new Rocket("Falcon 9", 9, RocketRange.EARTH, RocketStatus.ACTIVE, LAST, NEXT);
    given(repository.save(any(Rocket.class))).willReturn(saved);

    RocketResponse result = service.create(request);

    ArgumentCaptor<Rocket> captor = ArgumentCaptor.forClass(Rocket.class);
    then(repository).should().save(captor.capture());
    assertThat(captor.getValue().getName()).isEqualTo("Falcon 9");
    assertThat(captor.getValue().getStatus()).isEqualTo(RocketStatus.ACTIVE);
    assertThat(result.name()).isEqualTo("Falcon 9");
  }

  @Test
  void createThrows400WhenNameIsBlank() {
    RocketRequest request = new RocketRequest("", 9, RocketRange.EARTH, RocketStatus.ACTIVE, null, null);

    assertThatThrownBy(() -> service.create(request))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("400");
  }

  @Test
  void createThrows400WhenCapacityIsZero() {
    RocketRequest request = new RocketRequest("Falcon 9", 0, RocketRange.EARTH, RocketStatus.ACTIVE, null, null);

    assertThatThrownBy(() -> service.create(request))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("400");
  }

  @Test
  void createThrows400WhenRangeIsNull() {
    RocketRequest request = new RocketRequest("Falcon 9", 9, null, RocketStatus.ACTIVE, null, null);

    assertThatThrownBy(() -> service.create(request))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("400");
  }

  @Test
  void updateAppliesAllFieldsAndPersists() {
    Rocket existing = new Rocket("Old Name", 5, RocketRange.EARTH, RocketStatus.ACTIVE, null, null);
    given(repository.findById(1L)).willReturn(Optional.of(existing));
    given(repository.save(any(Rocket.class))).willReturn(existing);

    RocketRequest request = new RocketRequest("Falcon 9", 9, RocketRange.MARS, RocketStatus.MAINTENANCE, LAST, NEXT);
    service.update(1L, request);

    assertThat(existing.getName()).isEqualTo("Falcon 9");
    assertThat(existing.getCapacity()).isEqualTo(9);
    assertThat(existing.getRange()).isEqualTo(RocketRange.MARS);
    assertThat(existing.getStatus()).isEqualTo(RocketStatus.MAINTENANCE);
    assertThat(existing.getLastMaintenanceDate()).isEqualTo(LAST);
  }

  @Test
  void updateThrows404WhenNotFound() {
    given(repository.findById(99L)).willReturn(Optional.empty());
    RocketRequest request = new RocketRequest("Falcon 9", 9, RocketRange.EARTH, RocketStatus.ACTIVE, null, null);

    assertThatThrownBy(() -> service.update(99L, request))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("404");
  }

  @Test
  void deleteRemovesExistingRocket() {
    given(repository.existsById(1L)).willReturn(true);

    service.delete(1L);

    then(repository).should().deleteById(1L);
  }

  @Test
  void deleteThrows404WhenNotFound() {
    given(repository.existsById(99L)).willReturn(false);

    assertThatThrownBy(() -> service.delete(99L))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining("404");
  }
}
