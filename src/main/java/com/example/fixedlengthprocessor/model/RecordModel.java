package com.example.fixedlengthprocessor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordModel implements Serializable {
    private static final long serialVersionUID = 5577103633498684262L;
    private String trackId; // 36 characters
    private String pdfNaam; // 40 characters
    private String bestemming; // 3 characters
    private Integer aantalPaginas; // up to 2 characters (no leading zeros)

}
