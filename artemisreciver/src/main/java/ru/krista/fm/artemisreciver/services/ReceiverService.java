package ru.krista.fm.artemisreciver.services;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import ru.krista.fm.artemisreciver.domain.MessageLog;
import ru.krista.fm.artemisreciver.dtos.MessageDto;
import ru.krista.fm.artemisreciver.repositories.MessageLogRepositories;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReceiverService {

    private final MessageLogRepositories repositories;

    @JmsListener(destination = "${jms.queue}")
    public void receiveMessage(String message) {
        var time = LocalDateTime.now();

        var gson = new Gson();
        var mess =  gson.fromJson(message, MessageDto.class);

        var t2 = LocalDateTime.now();

        var rec = MessageLog.builder()
                .message(mess.getMessage())
                .startTransactionTime(time)
                .sendTime(mess.getTime())
                .build();

        var saved = repositories.save(rec);

        var t3 = LocalDateTime.now();

        saved.setEndTransactionTime(LocalDateTime.now());
        repositories.save(saved);

        var t4 = LocalDateTime.now();

        log.debug("Recived messagre: " + message);

        var t5 = LocalDateTime.now();

        System.out.println(Duration.between(t2, time));
        System.out.println(Duration.between(t3, t2));
        System.out.println(Duration.between(t4, t3));
        System.out.println(Duration.between(t5, t4));
    }
}
