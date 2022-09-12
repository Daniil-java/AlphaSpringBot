//package com.project.AlphaSpringBot.service;
//
//import org.springframework.stereotype.Service;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//
//
//@Service
//public class ReplyMessagesService {
//    private LocaleMessageService localeMessageService;
//
//    public ReplyMessagesService(LocaleMessageService messageService) {
//        this.localeMessageService = messageService;
//    }
//
//    public SendMessage getReplyMessage(long chatId, String replyMessage) {
//        SendMessage message = new SendMessage(chatId, localeMessageService.getMessage(replyMessage));
//        return message;
//    }
//
//    public SendMessage getReplyMessage(long chatId, String replyMessage, Object... args) {
//        SendMessage message = new SendMessage(chatId, localeMessageService.getMessage(replyMessage, args));
//        return message;
//    }
//}
