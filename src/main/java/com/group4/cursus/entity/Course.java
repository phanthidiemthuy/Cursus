package com.group4.cursus.entity;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
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
@Table(name = "course")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"subCategories"})
public class Course {
    @Id
    @Column(name = "course_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @Column(name = "course_title", nullable = false)
    private String courseTitle;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "requirements", nullable = false)
    private String requirements;

    @Column(name = "course_level", nullable = false)
    private String courseLevel;

    @Column(name = "thumbnail", nullable = false)
    private String thumbnail;

    @Column(name = "regular_price", nullable = false)
    private BigDecimal regularPrice;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "is_blocked", nullable = false)
    private int isBlocked;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "subcategory_id", nullable = false)
    private SubCategory subCategory;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    @JsonManagedReference
    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    private List<OrderItem> orderItems;

    @JsonManagedReference
    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    private List<Cart> carts;

    @JsonManagedReference
    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    private List<Enrollment> enrollments;
}
