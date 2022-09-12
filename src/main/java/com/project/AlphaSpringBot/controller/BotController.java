package com.project.AlphaSpringBot.controller;

import com.project.AlphaSpringBot.botapi.BotState;
import com.project.AlphaSpringBot.botapi.BotStateContext;
import com.project.AlphaSpringBot.cache.UserDataCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class BotController {
    private BotStateContext botStateContext;    //Управление состояниями
    private UserDataCache userDataCache;    //Хранилище информации пользователей

    public BotController(BotStateContext botStateContext, UserDataCache userDataCache) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
    }

    public SendMessage handleUpdate(Update update) {
        SendMessage replyMessage = null;

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        var userId = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "/start":
                botState = BotState.START;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId); //Отправляет пользователя на регистрацию
                break;
        }
        userDataCache.setUsersCurrentBotState(userId, botState);    // "Регистрирует" пользователя

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }
}
