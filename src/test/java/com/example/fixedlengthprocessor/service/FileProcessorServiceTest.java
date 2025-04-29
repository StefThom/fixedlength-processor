package com.example.fixedlengthprocessor.service;

import com.example.fixedlengthprocessor.model.RecordModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jms.core.JmsTemplate;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class FileProcessorServiceTest {

    private JmsTemplate jmsTemplate;
    private ObjectMapper springbootObjectMapper;
    private FileProcessorService service;

    @BeforeEach
    void setUp() {
        jmsTemplate = mock(JmsTemplate.class);
        springbootObjectMapper = new ObjectMapper();
        service = new FileProcessorService(jmsTemplate, springbootObjectMapper);
    }

    @Test
    void testProcessFile_sendsJsonMessagesToQueue() throws Exception {
        // Voorbeeldregel van 81 karakters: 36 + 40 + 3 + 2
        String line = "1731fa92-cd42-4330-b2d3-ca69fcd78f611731fa92-cd42-4330-b2d3-ca69fcd78f61.pdfNLD18";
        ByteArrayInputStream input = new ByteArrayInputStream((line + "\n").getBytes(StandardCharsets.UTF_8));

        service.processFile(input);

        // Capture het verstuurde bericht
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(jmsTemplate, times(1)).convertAndSend(eq("univocity.queue"), captor.capture());

        String json = captor.getValue();
        assertNotNull(json);

        // Parse JSON en assert velden
        RecordModel result = springbootObjectMapper.readValue(json, RecordModel.class);
        assertEquals("1731fa92-cd42-4330-b2d3-ca69fcd78f61", result.getTrackId());
        assertEquals("1731fa92-cd42-4330-b2d3-ca69fcd78f61.pdf", result.getPdfNaam());
        assertEquals("NLD", result.getBestemming());
        assertEquals(18, result.getAantalPaginas());
    }

    @Test
    void testProcessFile_invalidInteger_skipsLine() {
        // Laatste veld bevat geen integer
        String line = "1731fa92-cd42-4330-b2d3-ca69fcd78f611731fa92-cd42-4330-b2d3-ca69fcd78f61.pdfNLDAB"; // Ongeldige integer

        ByteArrayInputStream input = new ByteArrayInputStream((line + "\n").getBytes(StandardCharsets.UTF_8));

        service.processFile(input);

        // Expect: message naar errorQueue
        verify(jmsTemplate, times(1)).convertAndSend(anyString(), (Object) any());
    }

    @Test
    void testProcessFile_tooShortLine_skipsLine() {
        String shortLine = "Te kort"; // duidelijk korter dan 81

        ByteArrayInputStream input = new ByteArrayInputStream((shortLine + "\n").getBytes(StandardCharsets.UTF_8));

        service.processFile(input);

        // Expect: message naar errorQueue
        verify(jmsTemplate, times(1)).convertAndSend(anyString(), (Object) any());
    }

}
