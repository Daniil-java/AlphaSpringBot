package com.project.alphaspringbot.botapi.handlers.eat;

import com.project.alphaspringbot.botapi.BotState;
import com.project.alphaspringbot.botapi.InputMessageHandler;
import com.project.alphaspringbot.cache.UserDataCache;
import com.project.alphaspringbot.model.Food;
import com.project.alphaspringbot.service.ReplyMessagesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class EatHandler implements InputMessageHandler {
    @Autowired
    private UserDataCache userDataCache;
    @Autowired
    private ReplyMessagesService messagesService;


    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.EAT_START)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.EAT_NAME);
            userDataCache.setFoodMap(message.getFrom().getId());
        }

        String userAnswer = message.getText();
        var userId = message.getFrom().getId();
        long chatId = message.getChatId();

        Food food = userDataCache.getUsersCurrentFood(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(chatId);

        SendMessage replyToUser = new SendMessage(String.valueOf(chatId), "Что-то пошло не так ¯\\_(ツ)_/¯");

        if (botState.equals(BotState.EAT_NAME)) {
            food.setUserId(chatId);
            food.setRegistredAt(new Timestamp(System.currentTimeMillis()));
            userDataCache.setUsersCurrentBotState(userId, BotState.EAT_WEIGHT);
            return messagesService.getReplyMessage(chatId, "reply.EAT_NAME");
        }

        if (botState.equals(BotState.EAT_WEIGHT)) {
            food.setName(userAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CALORIES);
            return messagesService.getReplyMessage(chatId, "reply.EAT_WEIGHT");
        }

        if (botState.equals(BotState.EAT_CALORIES)) {
            Integer weight = 0;
            try {
                weight = Integer.valueOf(userAnswer);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CALORIES);
                return messagesService.getReplyMessage(chatId, "reply.NUMBER_EXC");
            }
            food.setWeight(weight);
            userDataCache.setUsersCurrentBotState(userId, BotState.EAT_PROTEINS);
            return messagesService.getReplyMessage(chatId, "reply.EAT_CALORIES");
        }

        if (botState.equals(BotState.EAT_PROTEINS)) {
            Integer calories = 0;
            try {
                calories = Integer.valueOf(userAnswer);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_PROTEINS);
                return messagesService.getReplyMessage(chatId, "reply.NUMBER_EXC");
            }
            food.setCalories(calories);
            userDataCache.setUsersCurrentBotState(userId, BotState.EAT_FATS);
            return messagesService.getReplyMessage(chatId, "reply.EAT_PROTEINS");
        }

        if (botState.equals(BotState.EAT_FATS)) {
            Integer proteins = 0;
            try {
                proteins = Integer.valueOf(userAnswer);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CARBOHYDRATES);
                return messagesService.getReplyMessage(chatId, "reply.NUMBER_EXC");
            }
            food.setProtein(proteins);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.EAT_FATS");
            userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CARBOHYDRATES);
        }

        if (botState.equals(BotState.EAT_CARBOHYDRATES)) {
            Integer fats = 0;
            try {
                fats = Integer.valueOf(userAnswer);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CARBOHYDRATES);
                return messagesService.getReplyMessage(chatId, "reply.NUMBER_EXC");
            }
            food.setFats(fats);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.EAT_CARBOHYDRATES");
            userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CLOSE);
        }

        if (botState.equals(BotState.EAT_CLOSE)) {
            Integer ch = 0;
            try {
                ch = Integer.valueOf(userAnswer);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CLOSE);
                return messagesService.getReplyMessage(chatId, "reply.NUMBER_EXC");
            }
            food.setCarbohydrates(ch);
            userDataCache.setUsersCurrentBotState(userId, BotState.PROCESSING);
            Integer calories = (userDataCache.getCaloriesLeft(chatId) - food.getCalories()*food.getWeight()/100);
            replyToUser = new SendMessage(String.valueOf(chatId), food.toStringForUser() +
                    "Каллорий осталось: " + calories);
            replyToUser.setReplyMarkup(getInlineMessageButtons());
            return replyToUser;
        }

        if (botState.equals(BotState.EAT_YESANSWER)) {
            userDataCache.saveFood(chatId);
            userDataCache.setUsersCurrentBotState(chatId, BotState.START);
            replyToUser = new SendMessage(String.valueOf(chatId), "Данные сохранены");
        }

        if (botState.equals(BotState.EAT_NOANSWER)) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.START);
            replyToUser = new SendMessage(String.valueOf(chatId), "Данные удалены");
        }

        return replyToUser;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.EAT_START;
    }

    private InlineKeyboardMarkup getInlineMessageButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonYes = new InlineKeyboardButton("Всё верно");
        InlineKeyboardButton buttonNo = new InlineKeyboardButton("Нет, неправильно");

        buttonYes.setCallbackData("buttonEatYes");
        buttonNo.setCallbackData("buttonEatNo");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonYes);
        keyboardButtonsRow1.add(buttonNo);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}
