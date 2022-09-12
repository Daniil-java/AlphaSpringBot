package com.project.AlphaSpringBot.cache;

import com.project.AlphaSpringBot.botapi.BotState;
import com.project.AlphaSpringBot.model.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

//По сути БД
@Component
public class UserDataCache implements DataCache{
    private Map<Long, BotState> usersBotStates = new HashMap<>();
    private Map<Long, User> usersData = new HashMap<>();

    @Override
    public void setUsersCurrentBotState(Long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(Long userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.START;
        }

        return botState;
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
}
