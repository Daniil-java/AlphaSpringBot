package com.project.AlphaSpringBot.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity(name="foodTest1Table")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "t_generator")
    @SequenceGenerator(name = "t_generator", sequenceName = "foodTest1Table", initialValue=1)
    private Long id;
    private String name;
    private int weight;
    private int calories; //На 100г
    private int protein;
    private int fats;
    private int carbohydrates;

    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                ", calories=" + calories +
                ", protein=" + protein +
                ", fats=" + fats +
                ", carbohydrates=" + carbohydrates +
                '}';
    }
}
