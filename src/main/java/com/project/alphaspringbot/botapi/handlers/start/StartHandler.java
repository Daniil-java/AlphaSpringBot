package com.project.alphaspringbot.botapi.handlers.start;

import com.project.alphaspringbot.botapi.BotState;
import com.project.alphaspringbot.botapi.InputMessageHandler;
import com.project.alphaspringbot.cache.UserDataCache;
import com.project.alphaspringbot.model.User;
import com.project.alphaspringbot.repository.UserRepository;
import com.project.alphaspringbot.service.ReplyMessagesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;

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
        long chatId = message.getChatId();
        registerUser(message);
        return messagesService.getReplyMessage(chatId,"reply.START");
    }

    @Override
    public BotState getHandlerName() {
        return BotState.START;
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
