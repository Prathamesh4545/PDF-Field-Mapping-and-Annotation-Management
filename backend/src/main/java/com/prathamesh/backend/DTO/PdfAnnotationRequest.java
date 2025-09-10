package com.prathamesh.backend.DTO;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PdfAnnotationRequest {
    private Long process;
    
    @JsonProperty("formId")
    private Long formId;
    
    @JsonProperty("fieldId")
    private Long fieldId;
    
    @JsonProperty("field_name")
    private String fieldName;
    
    @JsonProperty("field_header")
    private String fieldHeader;
    
    private double[] bbox;
    private int page;
    private double scale;
    
    @JsonProperty("field_type")
    private String fieldType;
    
    private Map<String, Object> metadata;
    public Long getProcess() { return process; }
    public void setProcess(Long process) { this.process = process; }
    
    public Long getFormId() { return formId; }
    public void setFormId(Long formId) { this.formId = formId; }
    
    public Long getFieldId() { return fieldId; }
    public void setFieldId(Long fieldId) { this.fieldId = fieldId; }
    
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    
    public String getFieldHeader() { return fieldHeader; }
    public void setFieldHeader(String fieldHeader) { this.fieldHeader = fieldHeader; }
    
    public double[] getBbox() { return bbox; }
    public void setBbox(double[] bbox) { this.bbox = bbox; }
    
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    
    public double getScale() { return scale; }
    public void setScale(double scale) { this.scale = scale; }
    
    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}