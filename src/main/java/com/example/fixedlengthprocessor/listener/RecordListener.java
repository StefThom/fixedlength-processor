package com.example.fixedlengthprocessor.listener;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class RecordListener {

    @JmsListener(destination = "univocity.queue")
    public void receiveMessage(String message) {
        System.out.println("Received Message: " + message);
    }
}
