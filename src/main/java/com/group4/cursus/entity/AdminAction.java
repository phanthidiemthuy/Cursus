package com.group4.cursus.entity;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin_action")
public class AdminAction {
    @Id
    @Column(name = "admin_action_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int adminActionId;

    @Column(name = "action_type", nullable = false)
    private String actionType;

    @Column(name = "target_id", nullable = false)
    private int targetId;

    @Column(name = "action_date", nullable = false)
    private LocalDate actionDate;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;
}
