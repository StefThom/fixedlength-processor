package com.example.fixedlengthprocessor.service;

import com.example.fixedlengthprocessor.domain.ProcessedRecord;
import com.example.fixedlengthprocessor.message.DeadLetterMessage;
import com.example.fixedlengthprocessor.model.RecordModel;
import com.example.fixedlengthprocessor.repository.ProcessedRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsTemplate;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

class FileProcessorServiceTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private ProcessedRecordRepository recordRepository;

    @InjectMocks
    private FileProcessorService fileProcessorService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessFile_sendsJsonMessagesToQueue() throws Exception {
        // Voorbeeldregel van 81 karakters: 36 + 40 + 3 + 2
        String line = "1731fa92-cd42-4330-b2d3-ca69fcd78f611731fa92-cd42-4330-b2d3-ca69fcd78f61.pdfNLD18";
        ByteArrayInputStream input = new ByteArrayInputStream((line + "\n").getBytes(StandardCharsets.UTF_8));

        fileProcessorService.processFile(input);

        // Verificatie: bericht naar queue
        verify(jmsTemplate, times(1)).convertAndSend(eq("univocity.queue"), any(RecordModel.class));
        // Verificatie: opslaan in database
        verify(recordRepository, times(1)).save(any(ProcessedRecord.class));
        // Geen DLQ aanroep
        verify(jmsTemplate, never()).convertAndSend(eq("univocity.dlq"), any(DeadLetterMessage.class));
    }

    @Test
    void testProcessFile_invalidInteger_skipsLine() {
        // Laatste veld bevat geen integer
        String line = "1731fa92-cd42-4330-b2d3-ca69fcd78f611731fa92-cd42-4330-b2d3-ca69fcd78f61.pdfNLDAB"; // Ongeldige integer

        ByteArrayInputStream input = new ByteArrayInputStream((line + "\n").getBytes(StandardCharsets.UTF_8));

        fileProcessorService.processFile(input);

        // Expect: message naar errorQueue
        verify(jmsTemplate, never()).convertAndSend(eq("univocity.queue"), any(RecordModel.class));
        verify(recordRepository, never()).save(any());
        verify(jmsTemplate, times(1)).convertAndSend(anyString(), (Object) any());
    }

    @Test
    void testProcessFile_tooShortLine_skipsLine() {
        String shortLine = "Te kort"; // duidelijk korter dan 81

        ByteArrayInputStream input = new ByteArrayInputStream((shortLine + "\n").getBytes(StandardCharsets.UTF_8));

        fileProcessorService.processFile(input);

        // Expect: message naar errorQueue
        verify(jmsTemplate, never()).convertAndSend(eq("univocity.queue"), any(RecordModel.class));
        verify(recordRepository, never()).save(any());
        verify(jmsTemplate, times(1)).convertAndSend(anyString(), (Object) any());
    }
}
