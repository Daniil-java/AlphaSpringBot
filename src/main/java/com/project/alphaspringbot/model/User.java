package com.project.alphaspringbot.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Data
@Entity(name="userTestTable")
public class User {

    @Id
    private Long chatId;
    private String firstName;
    private String lastName;
    private String userName;
    private Timestamp registredAt;

    private Double growth;

    private Double weight;

    private Sex sex;

    private int age;

    private int sport;

    private Integer cCalRestriction;

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", registerdAt=" + registredAt +
                '}';
    }

    public enum Sex {
        MAN,
        WOMAN
    }

}
