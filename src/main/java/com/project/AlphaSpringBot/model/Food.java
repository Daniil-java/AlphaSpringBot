package com.project.AlphaSpringBot.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity(name="foodTest1Table")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "t_generator")
//    @SequenceGenerator(name = "t_generator", sequenceName = "foodTest1Table", initialValue=1)
    private Long id;

    @Column(name = "userId")
    private Long userId;
    @Column(name = "name")
    private String name;
    @Column(name = "weight")
    private int weight;
    @Column(name = "calories")
    private int calories; //
    @Column(name = "protein")// На 100г
    private int protein;
    @Column(name = "fats")
    private int fats;
    @Column(name = "carbohydrates")
    private int carbohydrates;

    @Column(name = "registredAt")
    private Timestamp registredAt;

    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                ", calories=" + calories +
                ", protein=" + protein +
                ", fats=" + fats +
                ", carbohydrates=" + carbohydrates +
                ", registredAt=" + registredAt +
                '}';
    }
}
