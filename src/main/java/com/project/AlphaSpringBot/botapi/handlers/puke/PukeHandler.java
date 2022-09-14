package com.project.AlphaSpringBot.botapi.handlers.puke;

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
public class PukeHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;

    public PukeHandler(UserDataCache userDataCache, ReplyMessagesService messagesService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.PUKE_START)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.PUKE_DELETE);
        }
        return processFoodInput(message);
    }

    private SendMessage processFoodInput(Message message) {
        String userAnswer = message.getText();
        var userId = message.getFrom().getId();
        long chatId = message.getChatId();

        SendMessage replyToUser = new SendMessage();

        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.PUKE_DELETE)) {
            List<Food> foodList = userDataCache.getFoodList(userId, 3);
            String falseResult = "Список пуст";
            String result = "";
            int i = 0;
            for (Food f: foodList) {
                i++;
                result += i + "" + f.toStringForUser();
            }
            userDataCache.setUsersCurrentBotState(userId, BotState.PUKE_INPUT);
            return  replyToUser = new SendMessage(String.valueOf(chatId), result.equals("") ? falseResult : result);
        }

        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.PUKE_INPUT)) {
            List<Food> foodList = userDataCache.getFoodList(userId, 3);
            try {
                Integer number = Integer.valueOf(userAnswer);

                if (number > foodList.size() || number == 0) {
                    userDataCache.setUsersCurrentBotState(userId, BotState.START);
                    return  replyToUser = new SendMessage(String.valueOf(chatId), "ПШЁЛ ОТСЕДАВА, ПЁС");
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
