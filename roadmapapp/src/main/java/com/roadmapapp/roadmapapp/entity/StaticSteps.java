package com.roadmapapp.roadmapapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "StaticSteps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaticSteps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;
    private String Subcategory;
    private String StepJSONData;


    public String getSubcategory() {
        return Subcategory;
    }
}
