package com.project.alphaspringbot.repository;

import com.project.alphaspringbot.model.Food;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FoodRepository extends CrudRepository<Food, Long> {
    List<Food> findAllByUserId(Long userId);
}
