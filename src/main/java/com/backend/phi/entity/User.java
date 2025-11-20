package com.backend.phi.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(
        name = "User",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_user_nickname", columnNames = "nickname"),
                @UniqueConstraint(name = "uq_user_email", columnNames = "email")
        }
)
public class User {

    public enum Gender { M, F, OTHER }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false, nullable = false)
    private Long userId;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false, length = 30)
    private String name;

    // Store hashed password only; do not expose in DTOs
    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(name = "cred_score", nullable = false)
    private Integer credScore = 0;

    @Column(name = "user_score", nullable = false)
    private Integer userScore = 0;

    @Column
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    @Column
    private Gender gender; // M, F, OTHER

    // Temporary simple mapping. Plan: migrate to Hibernate Spatial Point.
    @Column(name = "last_location")
    private String lastLocation;

    // Prefer Instant to Timestamp; set on persist if DB default isn't applied.
    @Column(name = "user_created_at", nullable = false, updatable = false)
    private Instant userCreatedAt;

    public User() {}

    @PrePersist
    private void prePersist() {
        if (userCreatedAt == null) {
            userCreatedAt = Instant.now();
        }
    }

    // Getters and setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getCredScore() { return credScore; }
    public void setCredScore(Integer credScore) { this.credScore = credScore; }
    public Integer getUserScore() { return userScore; }
    public void setUserScore(Integer userScore) { this.userScore = userScore; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    public String getLastLocation() { return lastLocation; }
    public void setLastLocation(String lastLocation) { this.lastLocation = lastLocation; }
    public Instant getUserCreatedAt() { return userCreatedAt; }
    public void setUserCreatedAt(Instant userCreatedAt) { this.userCreatedAt = userCreatedAt; }
}