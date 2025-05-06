package com.example.fixedlengthprocessor.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "processed_records")
@Getter
@Setter
@NoArgsConstructor
public class ProcessedRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "track_Id", length = 36)
    private String trackId;
    @Column(name = "pdf_Naam", length = 40)
    private String pdfNaam;
    @Column(name = "bestemming", length = 3)
    private String bestemming;
    @Column(name = "aantal_Paginas", length = 2)
    private Integer aantalPaginas;
    @Column(name = "processed_At")
    private Instant processedAt;
}
