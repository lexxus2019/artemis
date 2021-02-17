package ru.krista.fm.artemisdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.krista.fm.artemisdemo.services.DispatcherService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
class ArtemisdemoApplicationTests {

    @Autowired
    private DispatcherService dispatcherService;

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    private final int COUNT = 1000;


    /*public class SendThread extends Thread {
        @Override
        public void run() {
            super.run();
            var i = atomicInteger.incrementAndGet();
            dispatcherService.sendMessage("Mess: " + i);
        }
    }*/

    @Test
    void contextLoads() throws InterruptedException {

        var task = new Runnable() {
            @Override
            public void run() {
                do {
                    var i = atomicInteger.incrementAndGet();
                    var name = Thread.currentThread().getName();
                    dispatcherService.sendMessage("Mess ("+name+"): " + i);
                } while (atomicInteger.get() < COUNT);
            }
        };

        var t1 = LocalDateTime.now();
        var thread1 = new Thread(task);

        var thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        do {
            Thread.sleep(100);
        } while (atomicInteger.get() < COUNT);

        thread1.interrupt();
        thread2.interrupt();

        var t2 = LocalDateTime.now();

        System.out.println(Duration.between(t2, t1));
    }

}
