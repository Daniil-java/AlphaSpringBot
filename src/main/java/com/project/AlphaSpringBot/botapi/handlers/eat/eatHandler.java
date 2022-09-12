package com.project.AlphaSpringBot.botapi.handlers.eat;

import com.project.AlphaSpringBot.botapi.BotState;
import com.project.AlphaSpringBot.botapi.InputMessageHandler;
import com.project.AlphaSpringBot.cache.UserDataCache;
import com.project.AlphaSpringBot.model.Food;
import com.project.AlphaSpringBot.repository.FoodRepository;
import com.project.AlphaSpringBot.repository.UserRepository;
import com.project.AlphaSpringBot.service.ReplyMessagesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@Slf4j
public class eatHandler implements InputMessageHandler {
    @Autowired
    private FoodRepository foodRepository;
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;


    public eatHandler(UserDataCache userDataCache,
                      ReplyMessagesService messagesService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.EAT_START)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.EAT_NAME);
        }
        return processFoodInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.EAT_START;
    }

    private SendMessage processFoodInput(Message inputMsg) {
        Food food = new Food();
        String userAnswer = inputMsg.getText();
        var userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();

        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = new SendMessage();

        if (botState.equals(BotState.EAT_NAME)) {
            food.setId(chatId);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.EAT_NAME");
            userDataCache.setUsersCurrentBotState(userId, BotState.EAT_WEIGHT);
        }

        if (botState.equals(BotState.EAT_WEIGHT)) {
            food.setName(userAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.EAT_WEIGHT");
            userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CALORIES);
        }

        if (botState.equals(BotState.EAT_CALORIES)) {
            try {
                Integer weight = Integer.valueOf(userAnswer);
                food.setWeight(weight);
                replyToUser = messagesService.getReplyMessage(chatId, "reply.EAT_CALORIES");
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_PROTEINS);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                replyToUser = messagesService.getReplyMessage(chatId, "reply.NUMBER_EXC");
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CALORIES);
            }
        }

        if (botState.equals(BotState.EAT_PROTEINS)) {
            try {
                Integer calories = Integer.valueOf(userAnswer);
                food.setCalories(calories);
                replyToUser = messagesService.getReplyMessage(chatId, "reply.EAT_PROTEINS");
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_FATS);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                replyToUser = messagesService.getReplyMessage(chatId, "reply.NUMBER_EXC");
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_PROTEINS);
            }
        }

        if (botState.equals(BotState.EAT_FATS)) {
            try {
                Integer proteins = Integer.valueOf(userAnswer);
                food.setProtein(proteins);
                replyToUser = messagesService.getReplyMessage(chatId, "reply.EAT_FATS");
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CARBOHYDRATES);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                replyToUser = messagesService.getReplyMessage(chatId, "reply.NUMBER_EXC");
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CARBOHYDRATES);
            }
        }

        if (botState.equals(BotState.EAT_CARBOHYDRATES)) {
            try {
                Integer fats = Integer.valueOf(userAnswer);
                food.setFats(fats);
                replyToUser = messagesService.getReplyMessage(chatId, "reply.EAT_CARBOHYDRATES");
                userDataCache.setUsersCurrentBotState(userId, BotState.PROCESSING);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                replyToUser = messagesService.getReplyMessage(chatId, "reply.NUMBER_EXC");
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CARBOHYDRATES);
            }
        }

        if (botState.equals(BotState.PROCESSING)) {
            try {
                Integer ch = Integer.valueOf(userAnswer);
                food.setFats(ch);
                replyToUser = messagesService.getReplyMessage(chatId, "reply.EAT_CARBOHYDRATES");
                userDataCache.setUsersCurrentBotState(userId, null);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                replyToUser = messagesService.getReplyMessage(chatId, "reply.NUMBER_EXC");
                userDataCache.setUsersCurrentBotState(userId, BotState.PROCESSING);
            }
        }

        foodRepository.save(food);

        return replyToUser;
    }
}
