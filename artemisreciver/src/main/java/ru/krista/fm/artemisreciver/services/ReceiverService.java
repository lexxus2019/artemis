package ru.krista.fm.artemisreciver.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReceiverService {

    @JmsListener(destination = "${jms.queue}")
    public void receiveMessage(String message) {
        log.debug("Recived messagre: " + message);
    }
}
