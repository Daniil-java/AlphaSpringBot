package com.project.alphaspringbot.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity(name="foodTest1Table")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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

    public String toStringForUser() {
        return "-----------------------\n" +
                name +
                ": К: " +calories*weight/100+
                " Б: " +protein*weight/100+
                " Ж: " +fats*weight/100+
                " У: " +carbohydrates*weight/100+
                "\n-----------------------\n";
    }
}
