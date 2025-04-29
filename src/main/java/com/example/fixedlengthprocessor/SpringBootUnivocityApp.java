package com.example.fixedlengthprocessor;

import com.example.fixedlengthprocessor.service.FileProcessorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication
public class SpringBootUnivocityApp implements CommandLineRunner {

    private final FileProcessorService fileProcessorService;

    public SpringBootUnivocityApp(FileProcessorService fileProcessorService) {
        this.fileProcessorService = fileProcessorService;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootUnivocityApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        var file = new ClassPathResource("csv/input.txt");
        fileProcessorService.processFile(file.getInputStream());
    }
}
