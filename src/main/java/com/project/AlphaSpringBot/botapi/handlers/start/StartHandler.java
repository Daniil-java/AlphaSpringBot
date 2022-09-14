package com.project.AlphaSpringBot.botapi.handlers.start;

import com.project.AlphaSpringBot.botapi.BotState;
import com.project.AlphaSpringBot.botapi.InputMessageHandler;
import com.project.AlphaSpringBot.cache.UserDataCache;
import com.project.AlphaSpringBot.model.User;
import com.project.AlphaSpringBot.repository.UserRepository;
import com.project.AlphaSpringBot.service.ReplyMessagesService;
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
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;

    public StartHandler(UserDataCache userDataCache,
                        ReplyMessagesService messagesService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.START;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        var userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();

        registerUser(inputMsg);

        SendMessage replyToUser = messagesService.getReplyMessage(chatId,"reply.START");

        return replyToUser;
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
