package com.project.AlphaSpringBot.botapi.handlers.info;

import com.project.AlphaSpringBot.botapi.BotState;
import com.project.AlphaSpringBot.botapi.InputMessageHandler;
import com.project.AlphaSpringBot.cache.UserDataCache;
import com.project.AlphaSpringBot.model.Food;
import com.project.AlphaSpringBot.service.ReplyMessagesService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Iterator;
import java.util.List;

@Component
public class infoHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;

    public infoHandler(UserDataCache userDataCache,
                       ReplyMessagesService messagesService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message inputMsg) {
        String userAnswer = inputMsg.getText();
        var userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();

        if (userDataCache.getUsersCurrentBotState(inputMsg.getFrom().getId()).equals(BotState.INFO_TODAY)) {
//            List<Food> foods = userDataCache.getTodayList(userId);
            List<Food> foods = userDataCache.getFoodList(userId, 1);
            return new SendMessage(String.valueOf(chatId), processInfo(foods));
        }
            return null;
    }

    private String processInfo(List<Food> foods) {
        String falseResult = "Список пуст";
        String result = "";
        for (Food f: foods) {
            result += result.concat(f.toStringForUser());
        }
        return result.equals("") ? falseResult : result;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.INFO;
    }
}
