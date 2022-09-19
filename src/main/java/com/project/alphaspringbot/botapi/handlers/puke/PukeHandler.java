package com.project.alphaspringbot.botapi.handlers.puke;

import com.project.alphaspringbot.botapi.BotState;
import com.project.alphaspringbot.botapi.InputMessageHandler;
import com.project.alphaspringbot.cache.UserDataCache;
import com.project.alphaspringbot.model.Food;
import com.project.alphaspringbot.service.ReplyMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class PukeHandler implements InputMessageHandler {
    @Autowired
    private UserDataCache userDataCache;
    @Autowired
    private ReplyMessagesService messagesService;


    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.PUKE_START)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.PUKE_DELETE);
        }

        String userAnswer = message.getText();
        var userId = message.getFrom().getId();
        long chatId = message.getChatId();

        SendMessage replyToUser = new SendMessage(String.valueOf(chatId), "Что-то пошло не так ¯\\_(ツ)_/¯");

        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.PUKE_DELETE)) {
            List<Food> foodList = userDataCache.getFoodList(userId, 3);
            String falseResult = "Список пуст";
            StringBuilder result = new StringBuilder();
            int i = 0;
            for (Food f: foodList) {
                i++;
                result.append(i).append(f.toStringForUser());
            }
            userDataCache.setUsersCurrentBotState(userId, BotState.PUKE_INPUT);
            return new SendMessage(String.valueOf(chatId), result.toString().equals("") ? falseResult : result.toString());
        }

        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.PUKE_INPUT)) {
            List<Food> foodList = userDataCache.getFoodList(userId, 3);
            try {
                Integer number = Integer.valueOf(userAnswer);

                if (number > foodList.size() || number == 0) {
                    userDataCache.setUsersCurrentBotState(userId, BotState.START);
                    return new SendMessage(String.valueOf(chatId), "ПШЁЛ ОТСЕДАВА, ПЁС");
                }

                userDataCache.deleteFoodById(foodList.get(number-1).getId());
                replyToUser = new SendMessage(String.valueOf(chatId), "DELETED");
                userDataCache.setUsersCurrentBotState(userId, BotState.START);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                messagesService.getReplyMessage(chatId, "reply.NUMBER_EXC");
                userDataCache.setUsersCurrentBotState(userId, BotState.START);
            }
        }

        return replyToUser;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.PUKE_START;
    }
}
