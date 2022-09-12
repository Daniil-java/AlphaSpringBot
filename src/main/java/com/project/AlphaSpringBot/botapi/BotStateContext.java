package com.project.AlphaSpringBot.botapi;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    //Заполняет мапу
    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    //Распределение запроса по обработчикам
    public SendMessage processInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isProcessing(currentState)) {
            return messageHandlers.get(BotState.PROCESSING);
        }

        return messageHandlers.get(currentState);
    }

    private boolean isProcessing(BotState currentState) {
        switch (currentState) {
            case PROCESSING:
            case EAT_START:
            case EAT_NAME:
            case EAT_COMPOUND:
            case EAT_WEIGHT:
            case PUKE_START:
            case PUKE_DELETE:
            case INFO_TODAY:
                return true;
            default:
                return false;
        }
    }

}
