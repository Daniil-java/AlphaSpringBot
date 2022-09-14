package com.project.AlphaSpringBot.cache;

import com.project.AlphaSpringBot.botapi.BotState;
import com.project.AlphaSpringBot.model.Food;
import com.project.AlphaSpringBot.model.User;
import com.project.AlphaSpringBot.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//По сути БД
@Component
public class UserDataCache implements DataCache{    //Прослойка между БД и приложением
    @Autowired
    private FoodRepository foodRepository;
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
        Food food = usersFood.get(userId);
        return food;
    }

    public void saveFood(Long userId) {
        Food food = usersFood.get(userId);
        foodRepository.save(food);
        food = new Food();
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

    public List<Food> getTodayList(Long userId) {
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

    public List<Food> getFoodList(Long userId, int days) {
        EntityManager entityManager = emf.createEntityManager();
        Query query = entityManager
                .createQuery("SELECT ft FROM food_Test1Table ft"
                        + " WHERE ft.userId=:userId and DATE(registred_at) > :days");
        query.setParameter("userId", userId);
        query.setParameter("days", days);
        List<Food> resultList = query.getResultList();
        entityManager.close();
        emf.close();
        return resultList;
    }
}
