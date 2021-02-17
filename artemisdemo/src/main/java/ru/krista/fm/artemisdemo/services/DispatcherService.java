package ru.krista.fm.artemisdemo.services;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import ru.krista.fm.artemisdemo.dtos.MessageDto;

import java.time.LocalDateTime;

@Service
public class DispatcherService {

    @Autowired
    JmsTemplate jmsTemplate;

    @Value("${jms.queue}")
    String jmsQueue;

    public void sendMessage(String message) {
        var mess = MessageDto.builder().message(message).time(LocalDateTime.now()).build();

        var gson = new Gson();

        jmsTemplate.setPubSubDomain(true);
        jmsTemplate.convertAndSend(jmsQueue, gson.toJson(mess));
    }
}
