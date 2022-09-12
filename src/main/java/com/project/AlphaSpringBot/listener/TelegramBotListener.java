package com.project.AlphaSpringBot.listener;

import com.project.AlphaSpringBot.config.BotConfig;
import com.project.AlphaSpringBot.controller.BotController;
import com.project.AlphaSpringBot.model.User;
import com.project.AlphaSpringBot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
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

    @Autowired
    private BotController botController;

    public TelegramBotListener(BotConfig config, BotController botController) {
        this.config = config;
        this.botController = botController;
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage replyMessageToUser = botController.handleUpdate(update);
        sendMessage(update.getMessage().getChatId(), replyMessageToUser.getText());
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


    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }


//    private void registerUser(Message message) {
//        if (userRepository.findById(message.getChatId()).isEmpty()) {
//            var chatId = message.getChatId();
//            var chat = message.getChat();
//
//            User user = new User();
//
//            user.setChatId(chatId);
//            user.setFirstName(chat.getFirstName());
//            user.setLastName(chat.getLastName());
//            user.setUserName(chat.getUserName());
//            user.setRegistredAt(new Timestamp(System.currentTimeMillis()));
//
//            userRepository.save(user);
//            log.info("user saved: " + user);
//
//        }
//    }
//
//    private User takeUser(Message message) {
//        User user = new User();
//        user.setChatId(message.getChatId());
//        return user;
//    }
//
//
//    private void startCommand(long chatId, String name) {
//
//        String answer = "Привет, " + name + ", приятно познакомиться";
//        sendMessage(chatId, answer);
//    }
//
//    private void sendMessage(long chatId, String textToSend) {
//        SendMessage message = new SendMessage();
//        message.setChatId(String.valueOf(chatId));
//        message.setText(textToSend);
//        try {
//            execute(message);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
