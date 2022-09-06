package com.project.AlphaSpringBot.Service;

import com.project.AlphaSpringBot.Config.BotConfig;
import com.project.AlphaSpringBot.Model.User;
import com.project.AlphaSpringBot.Model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    final BotConfig config;

    public TelegramBot(BotConfig config) {
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

    public boolean waitingWeight = false;
    public boolean waitingGrowth = false;

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if(!waitingWeight & !waitingGrowth) {
                switch (messageText) {
                    case "/start":
                        registerUser(update.getMessage());
                        startCommand(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    case "/weight":
                        setWaitingWeightTrue();
                        sendMessage(chatId, "Введите ваш вес");
                        break;
                    case "/growth":
                        setWaitingGrowthTrue();
                        sendMessage(chatId, "Введите ваш рост");
                        break;
                    default:
                        log.info(messageText);
                        System.out.println(waitingGrowth);
                        System.out.println(waitingWeight);
                        sendMessage(chatId, "Такой команды не существует");
                        break;
                }
            }

            if (waitingWeight) {
                registerWeight(update.getMessage());
                setWaitingWeightTrue();
            }
            if (waitingGrowth) {
                registerGrowth(update.getMessage());
                setWaitingGrowthTrue();
            }
        }
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

    private void registerWeight(Message message) {
        try {
            Double weight = Double.parseDouble(message.getText());
            User user = takeUser(message);
            user.setWeight(weight);
            userRepository.save(user);
        } catch (NumberFormatException e) {
            sendMessage(message.getChatId(), "Введите коректное число");
        }
    }

    private void registerGrowth(Message message) {
        try {
            Double growth = Double.parseDouble(message.getText());
            User user = takeUser(message);
            user.setGrowth(growth);
            userRepository.save(user);
        } catch (NumberFormatException e) {
            sendMessage(message.getChatId(), "Введите коректное число");
        }
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

    private User takeUser(Message message) {
        User user = new User();
        user.setChatId(message.getChatId());
        return user;
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
