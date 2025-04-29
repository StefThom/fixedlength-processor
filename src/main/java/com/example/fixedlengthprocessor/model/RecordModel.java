package com.example.fixedlengthprocessor.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RecordModel {
    private String trackId; // 36 characters
    private String pdfNaam; // 40 characters
    private String bestemming; // 3 characters
    private Integer aantalPaginas; // up to 2 characters (no leading zeros)

    public RecordModel() {
    }

    public RecordModel(String trackId, String pdfNaam, String bestemming, String aantalPaginasStr) {
        this.trackId = trackId.trim();
        this.pdfNaam = pdfNaam.trim();
        this.bestemming = bestemming.trim();
        this.aantalPaginas = Integer.parseInt(aantalPaginasStr.trim());
    }
}
