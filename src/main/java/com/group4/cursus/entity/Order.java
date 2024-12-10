package com.group4.cursus.entity;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "total_paid", nullable = false)
    private BigDecimal totalPaid;

    //private String txnRef;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;


    @OneToMany(mappedBy = "order")
    @JsonManagedReference
    private List<OrderItem> orderItems;
}
