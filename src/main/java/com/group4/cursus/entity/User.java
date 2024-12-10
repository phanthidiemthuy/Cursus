package com.group4.cursus.entity;


import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "address")
    private String address;

    @Column(name = "is_blocked")
    private boolean isBlocked;

    @Column(name = "is_approved")
    private boolean isApproved;

    @Column(name = "user_type")
    private String userType;

    public User(String fullName, String email, String password, LocalDate registrationDate, String address, boolean isBlocked, boolean isApproved, String userType) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.registrationDate = registrationDate;
        this.address = address;
        this.isBlocked = isBlocked;
        this.isApproved = isApproved;
        this.userType = userType;
    }
}