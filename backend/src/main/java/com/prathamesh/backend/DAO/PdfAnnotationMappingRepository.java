package com.prathamesh.backend.DAO;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.prathamesh.backend.Entity.PdfAnnotationMapping;

public interface PdfAnnotationMappingRepository extends JpaRepository<PdfAnnotationMapping, Long> {
    List<PdfAnnotationMapping> findByProcessAndFormId(Long process, Long formId);
    List<PdfAnnotationMapping> findByProcess(Long process);
}