package com.prathamesh.backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prathamesh.backend.DAO.PdfAnnotationMappingRepository;
import com.prathamesh.backend.DTO.PdfAnnotationRequest;
import com.prathamesh.backend.Entity.PdfAnnotationMapping;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class PdfAnnotationController {

    @Autowired
    private PdfAnnotationMappingRepository annotationRepo;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/pdf-annotation-mappings/bulk/")
    public ResponseEntity<?> saveBulkMappings(@RequestBody List<PdfAnnotationRequest> requests) {
        try {
            System.out.println("Received requests: " + requests.size());
            for (PdfAnnotationRequest req : requests) {
                System.out.println("Request data: " + req.getFieldName() + ", " + req.getFieldHeader() + ", " + req.getFieldType());
            }
            
            List<PdfAnnotationMapping> mappings = new ArrayList<>();
            
            for (PdfAnnotationRequest req : requests) {
                PdfAnnotationMapping mapping = new PdfAnnotationMapping();
                mapping.setProcess(req.getProcess());
                mapping.setFormId(req.getFormId());
                mapping.setFieldId(req.getFieldId());
                mapping.setFieldName(req.getFieldName());
                mapping.setFieldHeader(req.getFieldHeader());
                mapping.setFieldType(req.getFieldType());
                mapping.setPage(req.getPage());
                mapping.setScale(req.getScale());
                
                double[] bbox = req.getBbox();
                mapping.setX1(bbox[0]);
                mapping.setY1(bbox[1]);
                mapping.setX2(bbox[2]);
                mapping.setY2(bbox[3]);
                if (req.getMetadata() != null) {
                    mapping.setMetadata(objectMapper.writeValueAsString(req.getMetadata()));
                }
                
                mappings.add(mapping);
            }
            
            annotationRepo.saveAll(mappings);
            return ResponseEntity.ok("Mappings saved successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to save mappings: " + e.getMessage());
        }
    }

    @PostMapping("/app_admin/api/fetch-create-table/")
    public ResponseEntity<?> fetchMappings(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("Fetch request: " + request);
            Long processId = Long.valueOf(request.get("process_id").toString());
            Long formId = request.containsKey("form_id") ? Long.valueOf(request.get("form_id").toString()) : null;
            
            System.out.println("Searching for processId: " + processId + ", formId: " + formId);
            
            List<PdfAnnotationMapping> mappings;
            if (formId != null) {
                mappings = annotationRepo.findByProcessAndFormId(processId, formId);
            } else {
                mappings = annotationRepo.findByProcess(processId);
            }
            
            System.out.println("Found " + mappings.size() + " mappings");
            
            List<Map<String, Object>> response = new ArrayList<>();
            for (PdfAnnotationMapping mapping : mappings) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", mapping.getFieldId());
                
                Map<String, Object> annotation = new HashMap<>();
                Map<String, Double> bbox = new HashMap<>();
                bbox.put("x1", mapping.getX1());
                bbox.put("y1", mapping.getY1());
                bbox.put("x2", mapping.getX2());
                bbox.put("y2", mapping.getY2());
                
                annotation.put("bbox", bbox);
                annotation.put("page", mapping.getPage());
                annotation.put("field_id", mapping.getFieldId());
                annotation.put("field_name", mapping.getFieldName());
                annotation.put("field_header", mapping.getFieldHeader());
                annotation.put("process", mapping.getProcess());
                annotation.put("form_id", mapping.getFormId());
                
                item.put("annotation", annotation);
                item.put("field_name", mapping.getFieldName());
                item.put("field_type", mapping.getFieldType());
                item.put("field_header", mapping.getFieldHeader());
                item.put("process_id", mapping.getProcess().toString());
                item.put("form_id", mapping.getFormId());
                
                response.add(item);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to fetch mappings: " + e.getMessage());
        }
    }
}