package com.project.AlphaSpringBot.listener;

import com.project.AlphaSpringBot.config.BotConfig;
import com.project.AlphaSpringBot.controller.BotController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class TelegramBotListener extends TelegramLongPollingBot {
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
        sendMessage(botController.handleUpdate(update));
    }

    private void sendMessage(SendMessage message) {
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

}
