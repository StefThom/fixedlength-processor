package com.example.fixedlengthprocessor.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DeadLetterMessage {
    private String failedLine;
    private String error;
    private String timestamp;
}
