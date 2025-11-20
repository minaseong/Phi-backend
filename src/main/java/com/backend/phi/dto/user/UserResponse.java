package com.backend.phi.dto.user;

import java.time.Instant;
import java.time.LocalDate;

public class UserResponse {

    private Long userId;
    private String nickname;
    private String name;
    private String email;
    private Integer credScore;
    private Integer userScore;
    private LocalDate dob;
    private String gender;
    private String lastLocation;
    private Instant userCreatedAt;

    public UserResponse() {}

    public UserResponse(Long userId,
                        String nickname,
                        String name,
                        String email,
                        Integer credScore,
                        Integer userScore,
                        LocalDate dob,
                        String gender,
                        String lastLocation,
                        Instant userCreatedAt) {
        this.userId = userId;
        this.nickname = nickname;
        this.name = name;
        this.email = email;
        this.credScore = credScore;
        this.userScore = userScore;
        this.dob = dob;
        this.gender = gender;
        this.lastLocation = lastLocation;
        this.userCreatedAt = userCreatedAt;
    }

    // getters & setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getCredScore() { return credScore; }
    public void setCredScore(Integer credScore) { this.credScore = credScore; }

    public Integer getUserScore() { return userScore; }
    public void setUserScore(Integer userScore) { this.userScore = userScore; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getLastLocation() { return lastLocation; }
    public void setLastLocation(String lastLocation) { this.lastLocation = lastLocation; }

    public Instant getUserCreatedAt() { return userCreatedAt; }
    public void setUserCreatedAt(Instant userCreatedAt) { this.userCreatedAt = userCreatedAt; }
}