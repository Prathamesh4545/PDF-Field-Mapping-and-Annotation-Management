package com.prathamesh.backend.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.io.File;
import java.io.IOException;
import java.util.*;
import com.prathamesh.backend.Entity.*;

import com.prathamesh.backend.DAO.PdfFieldRepository;
import com.prathamesh.backend.DAO.PdfFileRepository;
import com.prathamesh.backend.DTO.PdfFieldRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = "http://localhost:5173")
public class PdfController {

    @Autowired
    private PdfFileRepository fileRepo;

    @Autowired
    private PdfFieldRepository fieldRepo;

    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPdf(@RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file provided");
            }
            
            long maxSize = 10 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                return ResponseEntity.badRequest().body("File size exceeds 10MB limit");
            }
            
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            String filePath = UPLOAD_DIR + file.getOriginalFilename();
            File dest = new File(filePath);
            file.transferTo(dest);

            pdfFiles pdfFile = new pdfFiles();
            pdfFile.setFileName(file.getOriginalFilename());
            pdfFile.setFilePath(filePath);
            fileRepo.save(pdfFile);

            return ResponseEntity.ok(Map.of("pdfId", pdfFile.getId(), "fileName", pdfFile.getFileName()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }

    @PostMapping("/{pdfId}/fields")
    public ResponseEntity<?> saveFields(@PathVariable Long pdfId, @RequestBody List<PdfFieldRequest> fields) {
        Optional<pdfFiles> pdfOpt = fileRepo.findById(pdfId);
        if (pdfOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        pdfFiles pdfFile = pdfOpt.get();
        List<PdfField> toSave = new ArrayList<>();
        for (PdfFieldRequest f : fields) {
            PdfField field = new PdfField();
            field.setFieldName(f.getName());
            field.setFieldType(f.getType());
            field.setPage(f.getPage());
            field.setX1(f.getX1());
            field.setY1(f.getY1());
            field.setX2(f.getX2());
            field.setY2(f.getY2());
            field.setPdfFile(pdfFile);
            toSave.add(field);
        }
        fieldRepo.saveAll(toSave);

        return ResponseEntity.ok("Fields saved successfully");
    }

    @GetMapping("/{pdfId}/fields")
    public ResponseEntity<?> getFields(@PathVariable Long pdfId) {
        List<PdfField> fields = fieldRepo.findByPdfFileId(pdfId);
        return ResponseEntity.ok(fields);
    }
}
