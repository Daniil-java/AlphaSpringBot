package com.project.alphaspringbot.cache;

import com.project.alphaspringbot.botapi.BotState;
import com.project.alphaspringbot.model.Food;
import com.project.alphaspringbot.model.User;
import com.project.alphaspringbot.repository.FoodRepository;
import com.project.alphaspringbot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

//По сути БД
@Component
public class UserDataCache implements DataCache{    //Прослойка между БД и приложением
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private UserRepository userRepository;

    private final EntityManagerFactory emf;
    private Map<Long, BotState> usersBotStates = new HashMap<>();
    private Map<Long, Food> usersFood = new HashMap<>();
    private Map<Long, User> usersData = new HashMap<>();

    public UserDataCache(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void setUsersCurrentBotState(Long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }
    public void setFoodMap(Long userId) {
        usersFood.put(userId, new Food());
    }

    @Override
    public BotState getUsersCurrentBotState(Long userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.START;
        }

        return botState;
    }

    public Food getUsersCurrentFood(Long userId) {
        return usersFood.get(userId);
    }

    public void saveFood(Long userId) {
        Food food = usersFood.get(userId);
        foodRepository.save(food);
    }

    @Override
    public User getUser(Long userId) {
        User user = usersData.get(userId);
        if (user == null) {
            user = new User();
        }
        return user;
    }

    @Override
    public void saveUser(Long userId, User user) {
        usersData.put(userId, user);
    }

    public List<Food> getAllTimeList(Long userId) {
        EntityManager entityManager = emf.createEntityManager();
        Query query = entityManager
                .createQuery("SELECT ft FROM foodTest1Table ft"
                        + " WHERE ft.userId=:userId ");
        query.setParameter("userId", userId);
        List<Food> resultList = query.getResultList();
        entityManager.close();
        emf.close();
        return resultList;
    }

    public List<Food> getFoodList(Long userId, double days) {
        LocalDate localDate = LocalDate.now();
        Date date = Date.from(localDate.minusDays((long) days).atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());

        EntityManager entityManager = emf.createEntityManager();
        Query query = entityManager
                .createQuery("SELECT ft FROM foodTest1Table ft"
                        + " WHERE ft.userId=:userId AND ft.registredAt >= :date");
        query.setParameter("userId", userId);
        query.setParameter("date", date);
        return query.getResultList();
    }

    public Integer getCaloriesLeft(Long userId) {
        List<Food> foodList = getFoodList(userId, 1);
        Integer caloriesLeft = userRepository.findById(userId).get().getcCalRestriction();
        for (Food food: foodList) {
            caloriesLeft -= food.getCalories();
        }
        return caloriesLeft;
    }

    public void deleteFoodById(Long foodId) {
        foodRepository.deleteById(foodId);
    }
}
