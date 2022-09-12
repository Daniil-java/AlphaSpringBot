package com.project.AlphaSpringBot.botapi.handlers.puke;

import com.project.AlphaSpringBot.botapi.BotState;
import com.project.AlphaSpringBot.botapi.InputMessageHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class pukeHandler implements InputMessageHandler {
    @Override
    public SendMessage handle(Message message) {
        return null;
    }

    @Override
    public BotState getHandlerName() {
        return null;
    }
}
