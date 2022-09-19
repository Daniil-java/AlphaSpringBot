package com.project.alphaspringbot.botapi.handlers.info;

import com.project.alphaspringbot.botapi.BotState;
import com.project.alphaspringbot.botapi.InputMessageHandler;
import com.project.alphaspringbot.cache.UserDataCache;
import com.project.alphaspringbot.model.Food;
import com.project.alphaspringbot.service.ReplyMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Component
public class InfoHandler implements InputMessageHandler {
    @Autowired
    private UserDataCache userDataCache;
    @Autowired
    private ReplyMessagesService messagesService;

    @Override
    public SendMessage handle(Message message) {
        var userId = message.getFrom().getId();
        long chatId = message.getChatId();

        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.INFO_TODAY)) {
            List<Food> foods = userDataCache.getFoodList(userId, 7);
            return new SendMessage(String.valueOf(chatId), processInfo(foods));
        }

        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.INFO_TOTAL)) {
            List<Food> foods = userDataCache.getFoodList(userId, 30); //Проверить вычитание дней
            userDataCache.setUsersCurrentBotState(userId, BotState.START);
            return new SendMessage(String.valueOf(chatId), processTotalInfo(foods));
        }
            return new SendMessage(String.valueOf(chatId), "Что-то пошло не так ¯\\_(ツ)_/¯");
    }

    private String processTotalInfo(List<Food> foods) {
        StringBuilder result = new StringBuilder();
        Map<LocalDate, Integer> map = new HashMap<>();
        for (Food food: foods) {
            LocalDate localDate = food.getRegistredAt()
                    .toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDate();
            Integer calories = map.containsKey(localDate) ? map.get(localDate) : 0;
            calories += food.getCalories()*food.getWeight()/100;
            map.put(localDate, calories);
        }

        for (Map.Entry<LocalDate, Integer> entry : map.entrySet()) {
            result.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return result.toString();
    }

    private String processInfo(List<Food> foods) {
        String falseResult = "Список пуст";
        StringBuilder result = new StringBuilder();
        for (Food f: foods) {
            result.append(f.toStringForUser());
        }
        return result.toString().equals("") ? falseResult : result.toString();
    }

    @Override
    public BotState getHandlerName() {
        return BotState.INFO;
    }
}
