package com.prathamesh.backend.DAO;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prathamesh.backend.Entity.pdfFiles;

public interface PdfFileRepository extends JpaRepository<pdfFiles, Long> {
    
}
