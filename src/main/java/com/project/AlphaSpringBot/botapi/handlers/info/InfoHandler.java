package com.project.AlphaSpringBot.botapi.handlers.info;

import com.project.AlphaSpringBot.botapi.BotState;
import com.project.AlphaSpringBot.botapi.InputMessageHandler;
import com.project.AlphaSpringBot.cache.UserDataCache;
import com.project.AlphaSpringBot.model.Food;
import com.project.AlphaSpringBot.service.ReplyMessagesService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Component
public class InfoHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;

    public InfoHandler(UserDataCache userDataCache,
                       ReplyMessagesService messagesService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        String userAnswer = message.getText();
        var userId = message.getFrom().getId();
        long chatId = message.getChatId();

        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.INFO_TODAY)) {
            List<Food> foods = userDataCache.getFoodList(userId, 7);
//            userDataCache.setUsersCurrentBotState(userId, BotState.START);
            SendMessage sendMessage = new SendMessage(String.valueOf(chatId), processInfo(foods));
            return sendMessage;
        }

        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.INFO_TOTAL)) {
            List<Food> foods = userDataCache.getFoodList(userId, 30); //Проверить вычитание дней
            userDataCache.setUsersCurrentBotState(userId, BotState.START);
            return new SendMessage(String.valueOf(chatId), processTotalInfo(foods));
        }
            return new SendMessage(String.valueOf(chatId), "Что-то пошло не так ¯\\_(ツ)_/¯");
    }

    private String processTotalInfo(List<Food> foods) {
        String result = "";
        Map<LocalDate, Integer> map = new HashMap<>();
//        Map<LocalDate, Integer> mapS = foods.stream().collect(Collectors.groupingBy(item -> item.getRegistredAt()
//                .toInstant()
//                .atZone(ZoneId.systemDefault())
//                .toLocalDate()
//                .with(foods.get())));

        for (Food food: foods) {
            LocalDate localDate = food.getRegistredAt()
                    .toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDate();
            Integer calories = map.containsKey(localDate) ? map.get(localDate) : 0;
            calories += food.getCalories()*food.getWeight()/100;
            map.put(localDate, calories);
        }

        for (Map.Entry<LocalDate, Integer> entry : map.entrySet()) {
            result += entry.getKey()+": "+entry.getValue() + "\n";
        }
        return result;
    }

    private String processInfo(List<Food> foods) {
        String falseResult = "Список пуст";
        String result = "";
        for (Food f: foods) {
            result += f.toStringForUser();
        }
        return result.equals("") ? falseResult : result;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.INFO;
    }
}
