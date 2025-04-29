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
    @Getter
    private final List<String> skippedLines = new ArrayList<>();

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
                    jmsTemplate.convertAndSend("univocity.queue", json);
                } catch (Exception e) {
                    log.warn("Fout bij verwerken regel: {} - {}", Arrays.toString(row), e.getMessage());
                    skippedLines.add(String.join("", row));
                }
            } else {
                log.warn("Overgeslagen regel met onjuist aantal velden: {}", Arrays.toString(row));
                skippedLines.add(String.join("", row));
            }
        }

        parser.stopParsing();
        log.info("Verwerking voltooid. {} regels overgeslagen.", skippedLines.size());

    }
}
