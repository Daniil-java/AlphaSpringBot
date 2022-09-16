package com.project.AlphaSpringBot.botapi.handlers.eat;

import com.project.AlphaSpringBot.botapi.BotState;
import com.project.AlphaSpringBot.botapi.InputMessageHandler;
import com.project.AlphaSpringBot.cache.UserDataCache;
import com.project.AlphaSpringBot.model.Food;
import com.project.AlphaSpringBot.service.ReplyMessagesService;
import lombok.extern.slf4j.Slf4j;
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
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;

    public EatHandler(UserDataCache userDataCache,
                      ReplyMessagesService messagesService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.EAT_START)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.EAT_NAME);
            userDataCache.setFoodMap(message.getFrom().getId());
        }

        if (userDataCache.getUsersCurrentBotState(message.getChatId()).equals(BotState.EAT_NOANSWER) ||
                userDataCache.getUsersCurrentBotState(message.getChatId()).equals(BotState.EAT_YESANSWER)) {
            return processFoodSave(message);
        }
        return processFoodInput(message);
    }

    private SendMessage processFoodSave(Message message) {
        String userAnswer = message.getText();
        long chatId = message.getChatId();
        Food food = userDataCache.getUsersCurrentFood(chatId);
        BotState botState = userDataCache.getUsersCurrentBotState(chatId);
        SendMessage replyToUser = new SendMessage();

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

    private SendMessage processFoodInput(Message inputMsg) {
        String userAnswer = inputMsg.getText();
        var userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();

        Food food = userDataCache.getUsersCurrentFood(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = new SendMessage();

        if (botState.equals(BotState.EAT_NAME)) {
            food.setUserId(chatId);
            food.setRegistredAt(new Timestamp(System.currentTimeMillis()));
            replyToUser = messagesService.getReplyMessage(chatId, "reply.EAT_NAME");
            userDataCache.setUsersCurrentBotState(userId, BotState.EAT_WEIGHT);
        }

        if (botState.equals(BotState.EAT_WEIGHT)) {
            food.setName(userAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.EAT_WEIGHT");
            userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CALORIES);
        }

        if (botState.equals(BotState.EAT_CALORIES)) {
            try {
                Integer weight = Integer.valueOf(userAnswer);
                food.setWeight(weight);
                replyToUser = messagesService.getReplyMessage(chatId, "reply.EAT_CALORIES");
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_PROTEINS);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                replyToUser = messagesService.getReplyMessage(chatId, "reply.NUMBER_EXC");
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CALORIES);
            }
        }

        if (botState.equals(BotState.EAT_PROTEINS)) {
            try {
                Integer calories = Integer.valueOf(userAnswer);
                food.setCalories(calories);
                replyToUser = messagesService.getReplyMessage(chatId, "reply.EAT_PROTEINS");
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_FATS);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                replyToUser = messagesService.getReplyMessage(chatId, "reply.NUMBER_EXC");
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_PROTEINS);
            }
        }

        if (botState.equals(BotState.EAT_FATS)) {
            try {
                Integer proteins = Integer.valueOf(userAnswer);
                food.setProtein(proteins);
                replyToUser = messagesService.getReplyMessage(chatId, "reply.EAT_FATS");
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CARBOHYDRATES);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                replyToUser = messagesService.getReplyMessage(chatId, "reply.NUMBER_EXC");
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CARBOHYDRATES);
            }
        }

        if (botState.equals(BotState.EAT_CARBOHYDRATES)) {
            try {
                Integer fats = Integer.valueOf(userAnswer);
                food.setFats(fats);
                replyToUser = messagesService.getReplyMessage(chatId, "reply.EAT_CARBOHYDRATES");
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CLOSE);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                replyToUser = messagesService.getReplyMessage(chatId, "reply.NUMBER_EXC");
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CARBOHYDRATES);
            }
        }

        if (botState.equals(BotState.EAT_CLOSE)) {
            try {
                Integer ch = Integer.valueOf(userAnswer);
                food.setCarbohydrates(ch);
                userDataCache.setUsersCurrentBotState(userId, BotState.PROCESSING);
                Integer calories = (userDataCache.getCaloriesLeft(chatId) - food.getCalories()*food.getWeight()/100);
                replyToUser = new SendMessage(String.valueOf(chatId), getCurrentFood(food) +
                        "Каллорий осталось: " + calories);
                replyToUser.setReplyMarkup(getInlineMessageButtons());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                replyToUser = messagesService.getReplyMessage(chatId, "reply.NUMBER_EXC");
                userDataCache.setUsersCurrentBotState(userId, BotState.EAT_CLOSE);
            }
        }

        return replyToUser;
    }

     private String getCurrentFood(Food food) {
        String result = food.toStringForUser();
        return result;
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
