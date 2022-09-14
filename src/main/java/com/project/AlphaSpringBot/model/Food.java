package com.project.AlphaSpringBot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name="foodTestTable")
public class Food {
    @Id
    private Long id;
    private String name;
    private double weight;
    //На 100г
    private int calories;
    private int protein;
    private int fats;
    private int carbohydrates;

//    public Food(String name, double weight, int calories) {
//        this.name = name;
//        this.weight = weight;
//        this.calories = calories;
//    }
//
//    public Food(Double weight, Integer calories) {
//        this.name = "Food";
//        this.weight = weight;
//        this.calories = calories;
//    }
//
//    public Food(String name, double weight, int calories, int protein, int fats, int carbohydrates) {
//        this.name = name;
//        this.weight = weight;
//        this.calories = calories;
//        this.protein = protein;
//        this.fats = fats;
//        this.carbohydrates = carbohydrates;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public int getFats() {
        return fats;
    }

    public void setFats(int fats) {
        this.fats = fats;
    }

    public int getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(int carbohydrates) {
        this.carbohydrates = carbohydrates;
    }
}
