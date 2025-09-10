import React, { useState, useEffect } from 'react';
import axios from 'axios';

export default function ExecutivePage() {
  const [mappings, setMappings] = useState([]);
  const [processId, setProcessId] = useState(49);
  const [formId, setFormId] = useState(20);
  const [selectedField, setSelectedField] = useState(null);

  const fetchMappings = async () => {
    try {
      console.log('Fetching mappings for:', { process_id: processId, form_id: formId });
      const response = await axios.post('http://localhost:8080/api/app_admin/api/fetch-create-table/', {
        process_id: processId,
        form_id: formId
      });
      console.log('Response:', response.data);
      setMappings(response.data);
    } catch (error) {
      console.error('Failed to fetch mappings:', error);
      console.error('Error response:', error.response?.data);
      alert(`Failed to fetch field mappings: ${error.response?.data || error.message}`);
    }
  };

  const handleFieldClick = (mapping) => {
    setSelectedField(mapping);
    console.log('Selected field:', mapping);
  };

  return (
    <div className="executive-page">
      <h2>Field Mappings Review</h2>
      
      <div className="fetch-controls">
        <label>Process ID: 
          <input type="number" value={processId} onChange={(e) => setProcessId(parseInt(e.target.value))} />
        </label>
        <label>Form ID: 
          <input type="number" value={formId} onChange={(e) => setFormId(parseInt(e.target.value))} />
        </label>
        <button onClick={fetchMappings}>Fetch Mappings</button>
      </div>

      <div className="mappings-list">
        <h3>Saved Field Mappings ({mappings.length})</h3>
        {mappings.map((mapping) => (
          <div 
            key={mapping.id} 
            className={`mapping-item ${selectedField?.id === mapping.id ? 'selected' : ''}`}
            onClick={() => handleFieldClick(mapping)}
          >
            <div className="field-info">
              <strong>{mapping.field_name}</strong> ({mapping.field_type})
              <div className="field-details">
                Header: {mapping.field_header} | Page: {mapping.annotation.page}
              </div>
              <div className="bbox-info">
                BBox: [{mapping.annotation.bbox.x1.toFixed(4)}, {mapping.annotation.bbox.y1.toFixed(4)}, 
                {mapping.annotation.bbox.x2.toFixed(4)}, {mapping.annotation.bbox.y2.toFixed(4)}]
              </div>
            </div>
          </div>
        ))}
      </div>

      {selectedField && (
        <div className="selected-field-details">
          <h4>Selected Field Details</h4>
          <pre>{JSON.stringify(selectedField, null, 2)}</pre>
        </div>
      )}
    </div>
  );
}