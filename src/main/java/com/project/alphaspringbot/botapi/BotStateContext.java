package com.project.alphaspringbot.botapi;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandlers = new EnumMap<>(BotState.class);

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
        if (isStarting(currentState)) {
            return messageHandlers.get(BotState.START);
        }
        if (isEating(currentState)) {
            return messageHandlers.get(BotState.EAT_START);
        }
        if (isInfo(currentState)) {
            return messageHandlers.get(BotState.INFO);
        }
        if (isStatePuke(currentState)) {
            return messageHandlers.get(BotState.PUKE_START);
        }
        if (isCaloriesRestrict(currentState)) {
            return messageHandlers.get(BotState.CALORIES);
        }
        if (isBody(currentState)) {
            return messageHandlers.get(BotState.BODY);
        }
        if (currentState.equals(BotState.HELP)) {
            return messageHandlers.get(BotState.HELP);
        }
        return messageHandlers.get(currentState);
    }

    private boolean isStarting(BotState currentState) {
        switch (currentState) {
            case START:
            case START_GROWTH_START:
            case START_SPORT_START:
            case START_CALCULATE:
            case START_SEX_MAN:
            case START_SEX_WOMAN:
            case START_SPORT_END:
            case START_GROWTH_END:
            case START_WEIGHT_END:
            case START_AGE_START:
            case START_SEX_START:
            case START_WEIGHT_START:
            case START_AGE_END:
                return true;
            default:
                return false;
        }
    }
    private boolean isCaloriesRestrict(BotState currentState) {
        switch (currentState) {
            case CALORIES:
            case CALORIES_START:
            case CALORIES_STOP:
                return true;
            default:
                return false;
        }
    }

    private boolean isBody(BotState currentState) {
        switch (currentState) {
            case BODY:
            case BODY_WEIGHT_START:
            case BODY_CALCULATE:
            case BODY_GROWTH_START:
            case BODY_SPORT_START:
            case BODY_WEIGHT_END:
            case BODY_GROWTH_END:
            case BODY_SPORT_END:
            case BODY_AGE_START:
            case BODY_AGE_END:
            case BODY_SEX_START:
            case BODY_SEX_MAN:
            case BODY_SEX_WOMAN:
                return true;
            default:
                return false;
        }
    }

    private boolean isEating(BotState currentState) {
        switch (currentState) {
            case EAT_START:
            case EAT_NAME:
            case EAT_WEIGHT:
            case EAT_CALORIES:
            case EAT_CARBOHYDRATES:
            case EAT_FATS:
            case EAT_PROTEINS:
            case EAT_CLOSE:
            case EAT_YESANSWER:
            case EAT_NOANSWER:
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

    private boolean isInfo(BotState currentState) {
        switch (currentState) {
            case INFO:
            case INFO_TODAY:
            case INFO_TOTAL:
                return true;
            default:
                return false;
        }
    }

}
