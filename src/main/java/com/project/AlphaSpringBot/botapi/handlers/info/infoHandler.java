package com.project.AlphaSpringBot.botapi.handlers.info;

import com.project.AlphaSpringBot.botapi.BotState;
import com.project.AlphaSpringBot.botapi.InputMessageHandler;
import com.project.AlphaSpringBot.cache.UserDataCache;
import com.project.AlphaSpringBot.model.Food;
import com.project.AlphaSpringBot.service.ReplyMessagesService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

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
//            String result = "";
//            List<Food> foodList = userDataCache.getTodayList(userId);
//            for (Food f: foodList) {
//                result.concat(f.toString() +"\n");
//            }
            return new SendMessage(String.valueOf(chatId), userDataCache.getTodayList(userId).toString());
        }
            return null;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.INFO;
    }
}
