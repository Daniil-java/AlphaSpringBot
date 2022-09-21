package com.project.alphaspringbot.botapi.handlers.help;

import com.project.alphaspringbot.botapi.BotState;
import com.project.alphaspringbot.botapi.InputMessageHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class HelpHandler implements InputMessageHandler {
    @Override
    public SendMessage handle(Message message) {
        return new SendMessage(String.valueOf(message.getChatId()),
                "Вас приветсвует приложение для ведения дневника питания - FIT.\n" +
                        "С моей помощью вы можете: \n" +
                        "/eat - записывать потребляему пищу\n" +
                        "/puke - удалять ранее записанные блюда(но только в течении 3х дней)\n" +
                        "/growth - указать ваш рост\n" +
                        "/weight - указать ваш вес\n" +
                        "/sport - указать уровень вашей физической активности\n" +
                        "/calculate - высчитать дневную норму каллорий" +
                        "/calories - задать ограничение потребляемых каллорий\n" +
                        "/total - показать информацию о потребленных, в течении месяца, каллориях\n" +
                        "/info_today - узнать информацию о сегодняшнем рационе"
        );
    }

    @Override
    public BotState getHandlerName() {
        return BotState.HELP;
    }
}
