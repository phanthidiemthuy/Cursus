package com.group4.cursus.entity;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "instructor")
@PrimaryKeyJoinColumn(name = "user_id")
@AllArgsConstructor
@NoArgsConstructor
public class Instructor extends User {

    @Column(name = "experience", nullable = false)
    private String experience;

    @Column(name = "salary", nullable = false)
    private BigDecimal salary;

    @JsonManagedReference
    @OneToMany(mappedBy = "instructor")
    private List<Course> courses;

    @JsonManagedReference
    @OneToMany(mappedBy = "instructor")
    private List<Payout> payouts;

    public Instructor(String fullName, String email, String password, LocalDate registrationDate,
                      String address, boolean isBlocked, boolean isApproved, String userType,
                      String experience, BigDecimal salary) {
        super(fullName, email, password, registrationDate, address, isBlocked, isApproved, userType);
        this.experience = experience;
        this.salary = salary;
    }


    public Instructor(String aliceJohnson, String mail, String password789, LocalDate of, String s, boolean b, boolean b1, String instructor) {
    }
}
