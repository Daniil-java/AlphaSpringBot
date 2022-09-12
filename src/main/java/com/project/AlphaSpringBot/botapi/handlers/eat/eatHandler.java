package com.project.AlphaSpringBot.botapi.handlers.eat;

import com.project.AlphaSpringBot.botapi.BotState;
import com.project.AlphaSpringBot.botapi.InputMessageHandler;
import com.project.AlphaSpringBot.cache.UserDataCache;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class eatHandler implements InputMessageHandler {
    private UserDataCache userDataCache;

    public eatHandler(UserDataCache userDataCache) {
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    private SendMessage processUsersInput(Message message) {
        var userId = message.getFrom().getId();
        long chatId = message.getChatId();

        SendMessage replyToUser = new SendMessage(chatId, "ТЕКСТ")
        userDataCache.setUsersCurrentBotState(userId,BotState.EAT_START);

        return replyToUser;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.START;
    }
}
