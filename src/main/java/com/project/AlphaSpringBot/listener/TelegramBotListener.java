package com.project.AlphaSpringBot.listener;

import com.project.AlphaSpringBot.config.BotConfig;
import com.project.AlphaSpringBot.model.User;
import com.project.AlphaSpringBot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;

@Slf4j
@Component
public class TelegramBotListener extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private final BotConfig config;
    private boolean waitingWeight = false;
    private boolean waitingGrowth = false;

    public TelegramBotListener(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if(!waitingWeight && !waitingGrowth) {
                switch (messageText) {
                    case "/start":
                        registerUser(update.getMessage());
                        startCommand(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    case "/weight":
                        setWaitingWeightTrue();
                        break;
                    case "/growth":
                        setWaitingGrowthTrue();
                        break;
                    case "/eat":
                        eatProcess(update.getMessage());
                        break;
                    default:
                        log.info(messageText);
                        sendMessage(chatId, "Такой команды не существует");
                        break;
                }
            }

            if (waitingWeight) {
                registerWeight(update.getMessage());
            }
            if (waitingGrowth) {
                registerGrowth(update.getMessage());
            }
        }
    }

    private void eatProcess(Message message) {
    }

    private void registerWeight(Message message) {
        Double weight = 0d;
        try {
            sendMessage(message.getChatId(), "Введите ваш вес");
            weight = Double.parseDouble(message.getText());
        } catch (NumberFormatException e) {
            sendMessage(message.getChatId(), "Введите коректное число");
        }
        User user = new User();
        user.setChatId(message.getChatId());
        user.setWeight(weight);
        userRepository.save(user);
        setWaitingWeightTrue();
    }

    private void registerGrowth(Message message) {
        Double growth = 0d;
        try {
            sendMessage(message.getChatId(), "Введите ваш рост");
            growth = Double.parseDouble(message.getText());
        } catch (NumberFormatException e) {
            sendMessage(message.getChatId(), "Введите коректное число");
        }
        User user = new User();
        user.setChatId(message.getChatId());
        user.setGrowth(growth);
        userRepository.save(user);
        setWaitingGrowthTrue();
    }

    public void setWaitingWeightTrue() {
        this.waitingWeight = true;
    }

    public void setWaitingGrowthTrue() {
        this.waitingGrowth = true;
    }

    public void setWaitingWeightFalse() {
        this.waitingWeight = false;
    }

    public void setWaitingGrowthFalse() {
        this.waitingGrowth = false;
    }

    private void registerUser(Message message) {
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

    private void startCommand(long chatId, String name) {

        String answer = "Привет, " + name + ", приятно познакомиться";
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
