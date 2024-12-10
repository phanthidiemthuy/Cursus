package com.group4.cursus.entity;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import lombok.*;

@Entity
@Setter
@Getter
@Table(name = "subcategory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"category, courses"})

public class SubCategory {
    public SubCategory(String subcategoryName, String description, Category category) {
        this.subcategoryName = subcategoryName;
        this.description = description;
        this.category = category;
    }

    @Id
    @Column(name = "subcategory_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subcategoryId;

    @Column(name = "subcategory_name", nullable = false)
    private String subcategoryName;

    @Column(name = "description", nullable = false)
    private String description;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;


    @JsonManagedReference
    @OneToMany(mappedBy = "subCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Course> courses;


}
