package com.example.fixedlengthprocessor.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeadLetterMessage implements Serializable {
    private static final long serialVersionUID = 742828825669687194L;
    private String failedLine;
    private String error;
    private Instant timestamp;
}
