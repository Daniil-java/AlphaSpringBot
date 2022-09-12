package com.project.AlphaSpringBot.repository;

import com.project.AlphaSpringBot.model.Food;
import org.springframework.data.repository.CrudRepository;

public interface FoodRepository extends CrudRepository<Food, Long> {
}
