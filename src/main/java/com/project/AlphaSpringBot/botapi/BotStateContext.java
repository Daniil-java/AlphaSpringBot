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
            return messageHandlers.get(BotState.EAT_START);
        }
        if (currentState.equals(BotState.INFO_TODAY)) {
            return messageHandlers.get(BotState.INFO);
        }
        if (currentState.equals(BotState.INFO_TOTAL)) {
            return messageHandlers.get(BotState.INFO);
        }
        if (isStatePuke(currentState)) {
            return messageHandlers.get(BotState.PUKE_START);
        }
        if (isCaloriesRestict(currentState)) {
            return messageHandlers.get(BotState.CALORIES);
        }
        return messageHandlers.get(currentState);
    }

    private boolean isCaloriesRestict(BotState currentState) {
        switch (currentState) {
            case CALORIES:
            case CALORIES_START:
            case CALORIES_STOP:
                return true;
            default:
                return false;
        }
    }

    private boolean isProcessing(BotState currentState) {
        switch (currentState) {
            case PROCESSING:
            case EAT_START:
            case EAT_NAME:
            case EAT_WEIGHT:
            case EAT_CALORIES:
            case EAT_CARBOHYDRATES:
            case EAT_FATS:
            case EAT_PROTEINS:
            case EAT_CLOSE:
                return true;
            default:
                return false;
        }
    }

    private boolean isStatePuke(BotState currentState) {
        switch (currentState) {
            case PUKE_START:
            case PUKE_DELETE:
            case PUKE_INPUT:
                return true;
            default:
                return false;
        }
    }

}
