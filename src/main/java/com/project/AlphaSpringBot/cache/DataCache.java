package com.project.AlphaSpringBot.cache;

import com.project.AlphaSpringBot.botapi.BotState;
import com.project.AlphaSpringBot.model.User;

//Отвечает за общение бота с разными пользователями
public interface DataCache {
    void setUsersCurrentBotState(Long userId, BotState botState);

    BotState getUsersCurrentBotState(Long userId);

    User getUser(Long userId);

    void saveUser(Long userId, User userProfileData);
}
