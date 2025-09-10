package com.prathamesh.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pdf_annotation_mappings")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PdfAnnotationMapping {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long process;
    private Long formId;
    private Long fieldId;
    private String fieldName;
    private String fieldHeader;
    private String fieldType;
    private int page;
    private double scale;
    
    private double x1, y1, x2, y2;
    
    @Column(columnDefinition = "TEXT")
    private String metadata;
}