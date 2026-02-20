package it.epicode.gestioneeventi.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime event_date;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    @Min(value = 1, message = "Available seats must be at least 1")
    private Integer available_seats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @Version
    private Long version;

    public Event() {
    }

    public Event(String title, String description, LocalDateTime event_date, String location, Integer available_seats, User organizer) {
        this.title = title;
        this.description = description;
        this.event_date = event_date;
        this.location = location;
        this.available_seats = available_seats;
        this.organizer = organizer;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEvent_date() {
        return event_date;
    }

    public void setEvent_date(LocalDateTime event_date) {
        this.event_date = event_date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getAvailable_seats() {
        return available_seats;
    }

    public void setAvailable_seats(Integer available_seats) {
        this.available_seats = available_seats;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }
}
