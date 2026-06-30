package dev.aiddbot.abjavareact.booking;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

  private final BookingService service;

  public BookingController(BookingService service) {
    this.service = service;
  }

  @GetMapping
  public List<BookingResponse> findAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  public BookingResponse findById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  public ResponseEntity<BookingResponse> create(@RequestBody BookingRequest request) {
    BookingResponse created = service.create(request);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @PostMapping("/{id}/cancel")
  public BookingResponse cancel(@PathVariable Long id) {
    return service.cancel(id);
  }
}
