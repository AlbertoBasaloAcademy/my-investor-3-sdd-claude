package dev.aiddbot.abjavareact.rocket;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "rocket")
public class Rocket {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private int capacity;

  @Enumerated(EnumType.STRING)
  @Column(name = "range", nullable = false)
  private RocketRange range;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RocketStatus status;

  @Column(name = "last_maintenance_date")
  private LocalDate lastMaintenanceDate;

  @Column(name = "next_maintenance_date")
  private LocalDate nextMaintenanceDate;

  protected Rocket() {}

  public Rocket(
      String name,
      int capacity,
      RocketRange range,
      RocketStatus status,
      LocalDate lastMaintenanceDate,
      LocalDate nextMaintenanceDate) {
    this.name = name;
    this.capacity = capacity;
    this.range = range;
    this.status = status;
    this.lastMaintenanceDate = lastMaintenanceDate;
    this.nextMaintenanceDate = nextMaintenanceDate;
  }

  public Long getId() { return id; }
  public String getName() { return name; }
  public int getCapacity() { return capacity; }
  public RocketRange getRange() { return range; }
  public RocketStatus getStatus() { return status; }
  public LocalDate getLastMaintenanceDate() { return lastMaintenanceDate; }
  public LocalDate getNextMaintenanceDate() { return nextMaintenanceDate; }

  public void setName(String name) { this.name = name; }
  public void setCapacity(int capacity) { this.capacity = capacity; }
  public void setRange(RocketRange range) { this.range = range; }
  public void setStatus(RocketStatus status) { this.status = status; }
  public void setLastMaintenanceDate(LocalDate lastMaintenanceDate) { this.lastMaintenanceDate = lastMaintenanceDate; }
  public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) { this.nextMaintenanceDate = nextMaintenanceDate; }
}
