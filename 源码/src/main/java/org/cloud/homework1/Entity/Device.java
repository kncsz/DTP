package org.cloud.homework1.Entity;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String status;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    // Constructors
    public Device() {}

    public Device(String name, String status, LocalDateTime lastUpdated) {
        this.name = name;
        this.status = status;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    // (Include getters and setters for userId)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
