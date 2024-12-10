package com.group4.cursus.entity;


import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import org.aspectj.weaver.ast.Or;

@Entity
@Table(name = "student")
@Data
@PrimaryKeyJoinColumn(name = "user_id")
public class Student extends User{
    @OneToMany(mappedBy = "student")
    @JsonManagedReference
    private List<Cart> carts;

    @OneToMany(mappedBy = "student")
    @JsonManagedReference
    private List<Order> orders;

    public Student(String fullName, String email, String password, LocalDate registrationDate, String address, boolean isBlocked, boolean isApproved, String userType) {
        super(fullName, email, password, registrationDate, address, isBlocked, isApproved, userType);
    }

    public Student() {

    }
}
