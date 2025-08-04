package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Employee")
@Table(name = "employees")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Employee {
    // Define fields for the Employee entity
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "position")
    private String position;

    @Column(name = "salary")
    private double salary;


}
