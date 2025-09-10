package com.prathamesh.backend.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prathamesh.backend.Entity.PdfField;

public interface PdfFieldRepository extends JpaRepository<PdfField, Long> {
    List<PdfField> findByPdfFileId(Long pdfId);
}
