package dev.aiddbot.abjavareact.booking;

import dev.aiddbot.abjavareact.launch.Launch;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "booking")
public class Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "launch_id", nullable = false)
  private Launch launch;

  @Column(name = "passenger_name", nullable = false)
  private String passengerName;

  @Column(name = "passenger_email", nullable = false)
  private String passengerEmail;

  @Column(name = "passenger_phone", nullable = false)
  private String passengerPhone;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookingStatus status;

  protected Booking() {}

  public Booking(Launch launch, String passengerName, String passengerEmail, String passengerPhone) {
    this.launch = launch;
    this.passengerName = passengerName;
    this.passengerEmail = passengerEmail;
    this.passengerPhone = passengerPhone;
    this.status = BookingStatus.CREATED;
  }

  public Long getId() { return id; }
  public Launch getLaunch() { return launch; }
  public String getPassengerName() { return passengerName; }
  public String getPassengerEmail() { return passengerEmail; }
  public String getPassengerPhone() { return passengerPhone; }
  public BookingStatus getStatus() { return status; }

  public void setLaunch(Launch launch) { this.launch = launch; }
  public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
  public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }
  public void setPassengerPhone(String passengerPhone) { this.passengerPhone = passengerPhone; }
  public void setStatus(BookingStatus status) { this.status = status; }
}
