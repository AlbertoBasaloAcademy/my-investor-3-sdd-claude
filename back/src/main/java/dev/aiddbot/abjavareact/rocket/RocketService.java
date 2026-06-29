package dev.aiddbot.abjavareact.rocket;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RocketService {

  private final RocketRepository repository;

  public RocketService(RocketRepository repository) {
    this.repository = repository;
  }

  public List<RocketResponse> findAll() {
    return repository.findAll().stream().map(this::toResponse).toList();
  }

  public RocketResponse findById(Long id) {
    return repository
        .findById(id)
        .map(this::toResponse)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rocket not found: " + id));
  }

  public RocketResponse create(RocketRequest request) {
    validate(request);
    Rocket rocket =
        new Rocket(
            request.name(),
            request.capacity(),
            request.rangeKm(),
            request.status(),
            request.lastMaintenanceDate(),
            request.nextMaintenanceDate());
    return toResponse(repository.save(rocket));
  }

  public RocketResponse update(Long id, RocketRequest request) {
    validate(request);
    Rocket rocket =
        repository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Rocket not found: " + id));
    rocket.setName(request.name());
    rocket.setCapacity(request.capacity());
    rocket.setRangeKm(request.rangeKm());
    rocket.setStatus(request.status());
    rocket.setLastMaintenanceDate(request.lastMaintenanceDate());
    rocket.setNextMaintenanceDate(request.nextMaintenanceDate());
    return toResponse(repository.save(rocket));
  }

  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rocket not found: " + id);
    }
    repository.deleteById(id);
  }

  private void validate(RocketRequest request) {
    if (request.name() == null || request.name().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rocket name is required");
    }
    if (request.capacity() <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Capacity must be positive");
    }
    if (request.rangeKm() <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Range must be positive");
    }
    if (request.status() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
    }
  }

  private RocketResponse toResponse(Rocket rocket) {
    return new RocketResponse(
        rocket.getId(),
        rocket.getName(),
        rocket.getCapacity(),
        rocket.getRangeKm(),
        rocket.getStatus().name(),
        rocket.getLastMaintenanceDate(),
        rocket.getNextMaintenanceDate());
  }
}
