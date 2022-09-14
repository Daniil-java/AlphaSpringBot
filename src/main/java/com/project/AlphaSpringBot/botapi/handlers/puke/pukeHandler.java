package com.project.AlphaSpringBot.botapi.handlers.puke;

import com.project.AlphaSpringBot.botapi.BotState;
import com.project.AlphaSpringBot.botapi.InputMessageHandler;
import com.project.AlphaSpringBot.cache.UserDataCache;
import com.project.AlphaSpringBot.service.ReplyMessagesService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class pukeHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;

    public pukeHandler(UserDataCache userDataCache, ReplyMessagesService messagesService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        return null;
    }

//    private String processInfo() {
//
//    }

    @Override
    public BotState getHandlerName() {
        return BotState.PUKE_START;
    }
}
