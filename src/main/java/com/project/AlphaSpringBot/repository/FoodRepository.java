package com.project.AlphaSpringBot.repository;

import com.project.AlphaSpringBot.model.Food;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FoodRepository extends CrudRepository<Food, Long> {
    List<Food> findAllByUserId(Long userId);
}
