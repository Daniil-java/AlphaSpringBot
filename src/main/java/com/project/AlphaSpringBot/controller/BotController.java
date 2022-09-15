package com.project.AlphaSpringBot.controller;

import com.project.AlphaSpringBot.botapi.BotState;
import com.project.AlphaSpringBot.botapi.BotStateContext;
import com.project.AlphaSpringBot.cache.UserDataCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            return processCallbackQuery(callbackQuery);
        }
        
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
            case "/eat":
                botState = BotState.EAT_START;
                break;
            case "/info_today":
                botState = BotState.INFO_TODAY;
                break;
            case "/puke":
                botState = BotState.PUKE_START;
                break;
            case "/calories":
                botState = BotState.CALORIES;
                break;
            case "/total":
                botState = BotState.INFO_TOTAL;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId); //Отправляет пользователя на регистрацию
                break;
        }
        userDataCache.setUsersCurrentBotState(userId, botState);    // "Регистрирует" пользователя

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }

    private SendMessage processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final var userId = buttonQuery.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;

        switch (buttonQuery.getData()) {
            case "buttonEatYes":
                botState = BotState.EAT_YESANSWER;
                break;
            case "buttonEatNo":
                botState = BotState.EAT_NOANSWER;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }

        userDataCache.setUsersCurrentBotState(chatId, botState);
        replyMessage = botStateContext.processInputMessage(botState, buttonQuery.getMessage());

        return replyMessage;
    }

    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }
}
