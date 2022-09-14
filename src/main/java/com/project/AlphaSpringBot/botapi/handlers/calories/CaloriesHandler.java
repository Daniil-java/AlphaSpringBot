package com.project.AlphaSpringBot.botapi.handlers.calories;

import com.project.AlphaSpringBot.botapi.BotState;
import com.project.AlphaSpringBot.botapi.InputMessageHandler;
import com.project.AlphaSpringBot.cache.UserDataCache;
import com.project.AlphaSpringBot.model.User;
import com.project.AlphaSpringBot.repository.UserRepository;
import com.project.AlphaSpringBot.service.ReplyMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class CaloriesHandler implements InputMessageHandler {
    @Autowired
    UserRepository userRepository;
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;

    public CaloriesHandler(UserDataCache userDataCache,
                           ReplyMessagesService messagesService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.CALORIES)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.CALORIES_START);
        }
        return processCaloriesRestriction(message);
    }

    private SendMessage processCaloriesRestriction(Message message) {
        String userAnswer = message.getText();
        var userId = message.getFrom().getId();
        long chatId = message.getChatId();

        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.CALORIES_START)) {
            userDataCache.setUsersCurrentBotState(userId, BotState.CALORIES_STOP);
            return new SendMessage(String.valueOf(chatId), "Введите количество, которым хотите ограничиться");
        }

        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.CALORIES_STOP)) {
            try {
                Integer calories = Integer.valueOf(userAnswer);
                User user = new User();
                user.setChatId(chatId);
                user.setcCalRestriction(calories);
                userRepository.save(user);
                userDataCache.setUsersCurrentBotState(userId, BotState.START);
                return new SendMessage(String.valueOf(chatId), "Ограничение установлено");
            } catch (NumberFormatException e) {
                e.printStackTrace();
                userDataCache.setUsersCurrentBotState(userId, BotState.CALORIES_STOP);
                return messagesService.getReplyMessage(chatId, "reply.NUMBER_EXC");
            }
        }
        return new SendMessage(String.valueOf(chatId), "Что-то пошло не так ¯\\_(ツ)_/¯");
    }

    @Override
    public BotState getHandlerName() {
        return BotState.CALORIES;
    }
}
