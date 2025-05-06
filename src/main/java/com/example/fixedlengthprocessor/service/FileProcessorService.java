package com.example.fixedlengthprocessor.service;

import com.example.fixedlengthprocessor.domain.ProcessedRecord;
import com.example.fixedlengthprocessor.message.DeadLetterMessage;
import com.example.fixedlengthprocessor.model.RecordModel;
import com.example.fixedlengthprocessor.repository.ProcessedRecordRepository;
import com.univocity.parsers.fixed.FixedWidthFields;
import com.univocity.parsers.fixed.FixedWidthParser;
import com.univocity.parsers.fixed.FixedWidthParserSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;


@Service
@Slf4j
public class FileProcessorService {

    private final JmsTemplate jmsTemplate;
    private final ProcessedRecordRepository recordRepository;

    private static final String MAIN_QUEUE = "univocity.queue";
    private static final String DEAD_LETTER_QUEUE = "univocity.dlq";

    public FileProcessorService(JmsTemplate jmsTemplate, ProcessedRecordRepository recordRepository) {
        this.jmsTemplate = jmsTemplate;
        this.recordRepository = recordRepository;
    }

    public void processFile(InputStream inputStream) {
        FixedWidthFields fields = new FixedWidthFields(36, 40, 3, 2);
        FixedWidthParserSettings settings = new FixedWidthParserSettings(fields);
        FixedWidthParser parser = new FixedWidthParser(settings);

        parser.beginParsing(new InputStreamReader(inputStream));
        String[] row;

        while ((row = parser.parseNext()) != null) {
            try {
                if (row.length != 4) throw new IllegalArgumentException("Incorrect number of fields");
                String trackId = row[0].trim();
                String pdfNaam = row[1].trim();
                String bestemming = row[2].trim();
                Integer aantalPaginas = Integer.parseInt(row[3].trim());

                // Verwerk record en stuur naar queue
                RecordModel model = new RecordModel(trackId, pdfNaam, bestemming, aantalPaginas);
                jmsTemplate.convertAndSend(MAIN_QUEUE, model);

                // Sla op in DB
                ProcessedRecord entity = new ProcessedRecord();
                entity.setTrackId(trackId);
                entity.setPdfNaam(pdfNaam);
                entity.setBestemming(bestemming);
                entity.setAantalPaginas(aantalPaginas);
                entity.setProcessedAt(Instant.now());

                recordRepository.save(entity);
            } catch (Exception e) {
                // Foutafhandeling DLQ
                sendToDeadLetterQueue(row, e.getMessage());
            }
        }
        parser.stopParsing();
        log.info("Verwerking afgerond.");
    }

    private void sendToDeadLetterQueue(String[] row, String errorMessage) {
        try {
            DeadLetterMessage dlqMessage = new DeadLetterMessage(Arrays.toString(row), errorMessage, Instant.now());

            jmsTemplate.convertAndSend(FileProcessorService.DEAD_LETTER_QUEUE, dlqMessage);
            log.warn("Verstuurd naar DLQ: {}", dlqMessage);
        } catch (Exception e) {
            log.error("Fout bij versturen naar DLQ: {}", e.getMessage());
        }
    }
}
