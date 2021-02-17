package ru.krista.fm.artemisreciver;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.krista.fm.artemisreciver.dtos.MessageDto;
import ru.krista.fm.artemisreciver.services.ReceiverService;

import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootTest
public class SaveTest {

    @Autowired
    private ReceiverService receiverService;

    @Test
    public void saveTest() {
        var t1 = LocalDateTime.now();
        var gson = new Gson();
        for (int i = 0; i < 1000; i++) {

            var dto = MessageDto.builder().message("test" + i).time(LocalDateTime.now()).build();
            receiverService.receiveMessage(gson.toJson(dto));
        }

        var t2 = LocalDateTime.now();

        System.out.println(Duration.between(t2, t1));
    }
}
