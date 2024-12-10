package com.group4.cursus.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "admin")
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {
    @Column(name = "level", nullable = false)
    private int level;

    @OneToMany(mappedBy = "admin")
    private List<AdminAction> adminActions;
}
