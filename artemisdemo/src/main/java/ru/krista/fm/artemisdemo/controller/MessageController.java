package ru.krista.fm.artemisdemo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.krista.fm.artemisdemo.services.DispatcherService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rest")
public class MessageController {

    private final DispatcherService dispatcherService;

    @GetMapping("/send")
    public ResponseEntity<String> send(String message) {
        dispatcherService.sendMessage(message);
        return new ResponseEntity<>("Message sent:" + message, HttpStatus.OK);
    }
}
