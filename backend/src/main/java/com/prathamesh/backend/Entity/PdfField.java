package com.prathamesh.backend.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pdf_fields")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PdfField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fieldName;
    private String fieldType;
    private int page;
    private double x1, y1, x2, y2;    

    @ManyToOne
    @JoinColumn(name = "pdf_file_id")
    private pdfFiles pdfFile;
}
