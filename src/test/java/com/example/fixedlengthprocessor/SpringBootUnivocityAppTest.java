package com.example.fixedlengthprocessor;

import com.example.fixedlengthprocessor.service.FileProcessorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SpringBootUnivocityAppTest {

    @Mock
    private FileProcessorService mockFileProcessorService;

    private SpringBootUnivocityApp springBootUnivocityAppUnderTest;

    @BeforeEach
    void setUp() {
        springBootUnivocityAppUnderTest = new SpringBootUnivocityApp(mockFileProcessorService);
    }

    @Test
    void testMain() {
        // Setup
        // Run the test
        SpringBootUnivocityApp.main(new String[]{"args"});

        // Verify the results
    }

    @Test
    void testRun() throws Exception {
        // Setup
        // Run the test
        springBootUnivocityAppUnderTest.run("args");

        // Verify the results
        verify(mockFileProcessorService).processFile(any(InputStream.class));
    }
}
