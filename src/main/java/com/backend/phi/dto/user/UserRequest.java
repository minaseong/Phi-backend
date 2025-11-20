package com.backend.phi.dto.user;

import java.time.LocalDate;

public class UserRequest {

    private String nickname;
    private String name;
    private String password;   // raw password for now
    private String email;
    private Integer credScore;
    private Integer userScore;
    private LocalDate dob;
    private String gender;     // "M", "F", "OTHER"
    private String lastLocation;

    public UserRequest() {}

    // getters & setters
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

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getLastLocation() { return lastLocation; }
    public void setLastLocation(String lastLocation) { this.lastLocation = lastLocation; }
}