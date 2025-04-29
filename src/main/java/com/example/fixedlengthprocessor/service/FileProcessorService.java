package com.example.fixedlengthprocessor.service;

import com.example.fixedlengthprocessor.model.RecordModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.univocity.parsers.fixed.FixedWidthFields;
import com.univocity.parsers.fixed.FixedWidthParser;
import com.univocity.parsers.fixed.FixedWidthParserSettings;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class FileProcessorService {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper springbootObjectMapper;

    private static final String MAIN_QUEUE = "univocity.queue";
    private static final String DEAD_LETTER_QUEUE = "univocity.dlq";

    public FileProcessorService(JmsTemplate jmsTemplate, ObjectMapper springbootObjectMapper) {
        this.jmsTemplate = jmsTemplate;
        this.springbootObjectMapper = springbootObjectMapper;
    }

    public void processFile(InputStream inputStream) {
        FixedWidthFields lengths = new FixedWidthFields(36, 40, 3, 2);
        FixedWidthParserSettings settings = new FixedWidthParserSettings(lengths);
        FixedWidthParser parser = new FixedWidthParser(settings);

        parser.beginParsing(new InputStreamReader(inputStream));

        String[] row;
        while ((row = parser.parseNext()) != null) {
            if (row.length == 4) {
                try {
                    RecordModel model = new RecordModel(row[0], row[1], row[2], row[3]);
                    String json = springbootObjectMapper.writeValueAsString(model);
                    jmsTemplate.convertAndSend(MAIN_QUEUE, json);
                } catch (Exception e) {
                    String failedLine = String.join("", row);
                    log.warn("Verwerken mislukt: {}, reden: {}", Arrays.toString(row), e.getMessage());
                    jmsTemplate.convertAndSend(DEAD_LETTER_QUEUE, failedLine);
                }
            } else {
                String invalidLine = String.join("", row);
                log.warn("Onjuist aantal velden, regel overgeslagen: {}", Arrays.toString(row));
                jmsTemplate.convertAndSend(DEAD_LETTER_QUEUE, invalidLine);
            }
        }

        parser.stopParsing();
        log.info("Verwerking afgerond.");

    }
}
