package com.project.alphaspringbot.botapi.handlers.calories;

import com.project.alphaspringbot.botapi.BotState;
import com.project.alphaspringbot.botapi.InputMessageHandler;
import com.project.alphaspringbot.cache.UserDataCache;
import com.project.alphaspringbot.model.User;
import com.project.alphaspringbot.repository.UserRepository;
import com.project.alphaspringbot.service.ReplyMessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class CaloriesHandler implements InputMessageHandler {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDataCache userDataCache;
    @Autowired
    private ReplyMessagesService messagesService;

    @Override
    public SendMessage handle(Message message) {

        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.CALORIES)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.CALORIES_START);
        }

        String userAnswer = message.getText();
        var userId = message.getFrom().getId();
        long chatId = message.getChatId();

        SendMessage replyMessage = new SendMessage(String.valueOf(chatId), "Что-то пошло не так ¯\\_(ツ)_/¯");

        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.CALORIES_START)) {
            userDataCache.setUsersCurrentBotState(userId, BotState.CALORIES_STOP);
            return new SendMessage(String.valueOf(chatId), "Введите количество, которым хотите ограничиться");
        }

        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.CALORIES_STOP)) {
            Integer calories = 0;
            try {
                calories = Integer.valueOf(userAnswer);
                userDataCache.setUsersCurrentBotState(userId, BotState.START);
                replyMessage = new SendMessage(String.valueOf(chatId), "Ограничение установлено");
            } catch (NumberFormatException e) {
                e.printStackTrace();
                userDataCache.setUsersCurrentBotState(userId, BotState.CALORIES_STOP);
                return messagesService.getReplyMessage(chatId, "reply.NUMBER_EXC");
            }
            User user = new User();
            user.setChatId(chatId);
            user.setcCalRestriction(calories);
            userRepository.save(user);
            return replyMessage;
        }
        return replyMessage;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.CALORIES;
    }
}
