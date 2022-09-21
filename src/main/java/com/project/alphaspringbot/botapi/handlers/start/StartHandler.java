package com.project.alphaspringbot.botapi.handlers.start;

import ch.qos.logback.core.joran.conditional.IfAction;
import com.project.alphaspringbot.botapi.BotState;
import com.project.alphaspringbot.botapi.InputMessageHandler;
import com.project.alphaspringbot.cache.UserDataCache;
import com.project.alphaspringbot.model.Food;
import com.project.alphaspringbot.model.User;
import com.project.alphaspringbot.repository.UserRepository;
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
import java.util.Objects;

@Slf4j
@Component
public class StartHandler implements InputMessageHandler {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDataCache userDataCache;
    @Autowired
    private ReplyMessagesService messagesService;

    @Override
    public SendMessage handle(Message message) {
        String userAnswer = message.getText();
        var userId = message.getFrom().getId();
        long chatId = message.getChatId();
        BotState botState = userDataCache.getUsersCurrentBotState(chatId);

        User user = userRepository.findById(chatId).get();
        SendMessage replyMessage = new SendMessage(String.valueOf(chatId), "Что-то пошло не так ¯\\_(ツ)_/¯");

        if (botState.equals(BotState.START)) {
            userDataCache.setUsersCurrentBotState(userId, BotState.START_AGE_END);
            registerUser(message);
            replyMessage.setText("Укажите ваш возраст");
        }

        if (botState.equals(BotState.START_AGE_END)) {
            Integer age = 0;
            try {
                age = Integer.valueOf(userAnswer);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                userDataCache.setUsersCurrentBotState(userId, BotState.START_AGE_END);
                replyMessage.setText("Укажите корректное число");
                return replyMessage;
            }
            user.setAge(age);
            userRepository.save(user);
            replyMessage.setText("Укажите ваш пол");
            replyMessage.setReplyMarkup(getInlineMessageButtons());
            return replyMessage;
        }

        if (botState.equals(BotState.START_SEX_WOMAN)) {
            user.setSex(User.Sex.WOMAN);
            userRepository.save(user);
            userDataCache.setUsersCurrentBotState(userId, BotState.START_WEIGHT_END);
            replyMessage.setText("Укажите ваш вес");
            return replyMessage;
        }

        if (botState.equals(BotState.START_SEX_MAN)) {
            user.setSex(User.Sex.MAN);
            userRepository.save(user);
            userDataCache.setUsersCurrentBotState(chatId, BotState.START_WEIGHT_END);
            replyMessage.setText("Укажите ваш вес");
            return replyMessage;
        }

        if (botState.equals(BotState.START_WEIGHT_END)) {
            Double weight = 0d;
            try {
                weight = Double.valueOf(userAnswer);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                userDataCache.setUsersCurrentBotState(userId, BotState.START_WEIGHT_END);
                replyMessage.setText("Укажите корректное число");
                return replyMessage;
            }
            user.setWeight(weight);
            userRepository.save(user);
            replyMessage.setText("Укажите ваш рост");
            userDataCache.setUsersCurrentBotState(userId, BotState.START_GROWTH_END);
            return replyMessage;
        }

        if (botState.equals(BotState.START_GROWTH_END)) {
            Double growth = 0d;
            try {
                growth = Double.valueOf(userAnswer);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                userDataCache.setUsersCurrentBotState(userId, BotState.START_GROWTH_END);
                replyMessage.setText("Укажите корректное число");
                return replyMessage;
            }
            user.setGrowth(growth);
            userRepository.save(user);
            userDataCache.setUsersCurrentBotState(userId, BotState.START_SPORT_END);
            replyMessage.setText("Укажите уровень вашей физической активности от 0 до 4");
            return replyMessage;
        }

        if (botState.equals(BotState.START_SPORT_END)) {
            Integer sport = 0;
            try {
                sport = Integer.valueOf(userAnswer);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                userDataCache.setUsersCurrentBotState(userId, BotState.START_SPORT_END);
                replyMessage.setText("Укажите корректное число");
                return replyMessage;
            }
            user.setSport(sport);
            userRepository.save(user);

            if (user.getAge() == 0 || Objects.isNull(user.getGrowth()) || Objects.isNull(user.getWeight()) || Objects.isNull(user.getSex())) {
                replyMessage.setText("Вы не указали возраст, пол, вес или рост.");
            } else {
                if (user.getSex().equals(User.Sex.MAN)) {
                    Double result;
                    result = 10 * user.getWeight() + 6.25 * user.getGrowth() - 5 * user.getAge() + 5;
                    replyMessage.setText("Норма каллорий: " + Math.round(result));
                } else if (user.getSex().equals(User.Sex.WOMAN)) {
                    Double result;
                    result = 10 * user.getWeight() + 6.25 * user.getGrowth() - 5 * user.getAge() - 161;
                    replyMessage.setText("Норма каллорий: " + Math.round(result));
                }
            }
            userDataCache.setUsersCurrentBotState(userId, BotState.PROCESSING);
            return replyMessage;
        }

        return replyMessage;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.START;
    }

    private InlineKeyboardMarkup getInlineMessageButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton buttonMan = new InlineKeyboardButton("Мужской");
        InlineKeyboardButton buttonWoman = new InlineKeyboardButton("Женский");

        buttonMan.setCallbackData("buttonStartMan");
        buttonWoman.setCallbackData("buttonStartWoman");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonMan);
        keyboardButtonsRow1.add(buttonWoman);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private void registerUser(Message message) { //Регистрация пользователя в БД
        if (userRepository.findById(message.getChatId()).isEmpty()) {
            var chatId = message.getChatId();
            var chat = message.getChat();

            User user = new User();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegistredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("user saved: " + user);
        }
    }
}
