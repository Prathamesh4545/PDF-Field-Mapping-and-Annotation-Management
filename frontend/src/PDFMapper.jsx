import React, { useEffect, useRef, useState } from "react";
import * as pdfjs from "pdfjs-dist";
import axios from "axios";

pdfjs.GlobalWorkerOptions.workerSrc = new URL(
  'pdfjs-dist/build/pdf.worker.min.mjs',
  import.meta.url,
).toString();

export default function PDFMapper({ pdfUrl }) {
  const pdfCanvasRef = useRef(null);
  const overlayRef = useRef(null);

  const [pdf, setPdf] = useState(null);
  const [pageNum, setPageNum] = useState(1);
  const [boxes, setBoxes] = useState([]);
  const [drawing, setDrawing] = useState(false);
  const [start, setStart] = useState(null);
  const [currentSelection, setCurrentSelection] = useState(null);
  const [pdfId, setPdfId] = useState(null);
  const [currentPdfUrl, setCurrentPdfUrl] = useState(pdfUrl);
  const [uploadedFile, setUploadedFile] = useState(null);
  const [sampleUploaded, setSampleUploaded] = useState(false);
  const [processId, setProcessId] = useState(49);
  const [formId, setFormId] = useState(20);
  const [scale, setScale] = useState(1.5);
  const renderTaskRef = useRef(null);

  const handleFileUpload = async (file) => {
    try {
      const maxSize = 10 * 1024 * 1024;
      if (file.size > maxSize) {
        alert(`File size (${(file.size / 1024 / 1024).toFixed(2)}MB) exceeds the 10MB limit.`);
        return;
      }

      const formData = new FormData();
      formData.append("file", file);

      const res = await axios.post("http://localhost:8080/api/pdf/upload", formData, {
        headers: { "Content-Type": "multipart/form-data" }
      });

      setPdfId(res.data.pdfId);
      setUploadedFile(file);
      
      const fileUrl = URL.createObjectURL(file);
      setCurrentPdfUrl(fileUrl);
      
      setBoxes([]);
      setPageNum(1);
      setPdf(null);
    } catch (error) {
      console.error("Failed to upload PDF:", error);
      console.error("Error response:", error.response?.data);
      alert(`Failed to upload PDF: ${error.response?.data || error.message}`);
    }
  };

  useEffect(() => {
    if (!uploadedFile && pdfUrl) {
      setCurrentPdfUrl(pdfUrl);
    }
  }, [pdfUrl, uploadedFile]);

  useEffect(() => {
    if (!currentPdfUrl) return;
    const loadPdf = async () => {
      try {
        const loadingTask = pdfjs.getDocument(currentPdfUrl);
        const pdfDoc = await loadingTask.promise;
        setPdf(pdfDoc);
      } catch (error) {
        console.error("Failed to load PDF:", error);
      }
    };
    loadPdf();
  }, [currentPdfUrl]);

  useEffect(() => {
    if (!pdf) return;
    const renderPage = async () => {
      if (renderTaskRef.current) {
        renderTaskRef.current.cancel();
      }

      const page = await pdf.getPage(pageNum);
      const viewport = page.getViewport({ scale: 1.5 });

      const canvas = pdfCanvasRef.current;
      const context = canvas.getContext("2d");
      canvas.width = viewport.width;
      canvas.height = viewport.height;

      const newRenderTask = page.render({ canvasContext: context, viewport });
      renderTaskRef.current = newRenderTask;
      
      try {
        await newRenderTask.promise;
        const overlay = overlayRef.current;
        overlay.width = viewport.width;
        overlay.height = viewport.height;
        drawBoxes();
      } catch (error) {
        if (error.name !== 'RenderingCancelledException') {
          console.error('Render error:', error);
        }
      }
    };
    renderPage();
  }, [pdf, pageNum, boxes, currentSelection]);

  const drawBoxes = () => {
    const overlay = overlayRef.current;
    const ctx = overlay.getContext("2d");
    ctx.clearRect(0, 0, overlay.width, overlay.height);

    boxes.filter(b => b.page === pageNum).forEach(b => {
      ctx.strokeStyle = "red";
      ctx.lineWidth = 2;
      ctx.setLineDash([]);
      ctx.strokeRect(b.x1, b.y1, b.x2 - b.x1, b.y2 - b.y1);
      ctx.fillStyle = "rgba(255,0,0,0.2)";
      ctx.fillRect(b.x1, b.y1, b.x2 - b.x1, b.y2 - b.y1);
      ctx.fillStyle = "black";
      ctx.font = "12px Arial";
      ctx.fillText(b.name, b.x1 + 4, b.y1 - 4);
    });

    if (currentSelection) {
      const { start, end } = currentSelection;
      ctx.strokeStyle = "blue";
      ctx.lineWidth = 2;
      ctx.setLineDash([5, 5]);
      ctx.strokeRect(
        Math.min(start.x, end.x),
        Math.min(start.y, end.y),
        Math.abs(end.x - start.x),
        Math.abs(end.y - start.y)
      );
      ctx.fillStyle = "rgba(0,0,255,0.1)";
      ctx.fillRect(
        Math.min(start.x, end.x),
        Math.min(start.y, end.y),
        Math.abs(end.x - start.x),
        Math.abs(end.y - start.y)
      );
    }
  };

  const handleMouseDown = (e) => {
    setDrawing(true);
    setStart({ x: e.nativeEvent.offsetX, y: e.nativeEvent.offsetY });
  };

  const handleMouseMove = (e) => {
    if (!drawing) return;
    const current = { x: e.nativeEvent.offsetX, y: e.nativeEvent.offsetY };
    setCurrentSelection({ start, end: current });
  };

  const handleMouseUp = (e) => {
    if (!drawing) return;
    setDrawing(false);
    setCurrentSelection(null);

    const end = { x: e.nativeEvent.offsetX, y: e.nativeEvent.offsetY };
    const fieldName = prompt("Enter field name:");
    const fieldHeader = prompt("Enter field header:");
    const fieldType = prompt("Enter field type (CharField, DateField, etc.):");
    const required = confirm("Is this field required?");
    const maxLength = prompt("Enter max length (or 0 for no limit):") || "0";

    if (!fieldName || !fieldHeader || !fieldType) {
      alert("All fields are required!");
      return;
    }

    const newBox = {
      process: processId,
      formId: formId,
      fieldId: Date.now(),
      field_name: fieldName,
      field_header: fieldHeader,
      bbox: [start.x, start.y, end.x, end.y],
      page: pageNum,
      scale: scale,
      field_type: fieldType,
      metadata: {
        required: required,
        max_length: parseInt(maxLength)
      },
      name: fieldName,
      type: fieldType,
      x1: start.x,
      y1: start.y,
      x2: end.x,
      y2: end.y
    };

    setBoxes([...boxes, newBox]);
  };

  const saveToBackend = async () => {
    if (boxes.length === 0) {
      alert("No fields to save. Please map some fields first.");
      return;
    }

    try {
      console.log("Sending data:", boxes);
      await axios.post("http://localhost:8080/api/pdf-annotation-mappings/bulk/", boxes);
      alert("Field mappings saved successfully!");
    } catch (error) {
      console.error("Failed to save fields:", error);
      console.error("Error response:", error.response?.data);
      alert(`Failed to save fields: ${error.response?.data || error.message}`);
    }
  };

  return (
    <div className="pdf-mapper">
      <h2>PDF Field Mapper</h2>
      
      <div className="upload-section">
        <input 
          type="file" 
          accept=".pdf" 
          onChange={(e) => e.target.files[0] && handleFileUpload(e.target.files[0])}
          className="file-input"
        />
        <div className="config-inputs">
          <label>Process ID: 
            <input type="number" value={processId} onChange={(e) => setProcessId(parseInt(e.target.value))} />
          </label>
          <label>Form ID: 
            <input type="number" value={formId} onChange={(e) => setFormId(parseInt(e.target.value))} />
          </label>
        </div>
        <span className="upload-text">Upload PDF (Max 10MB) or use sample PDF below</span>
      </div>

      {pdf && (
        <>
          <div className="pdf-container">
            <canvas ref={pdfCanvasRef} className="pdf-canvas" />
            <canvas
              ref={overlayRef}
              className="overlay-canvas"
              onMouseDown={handleMouseDown}
              onMouseMove={handleMouseMove}
              onMouseUp={handleMouseUp}
            />
          </div>
          <div className="controls">
            <button disabled={pageNum <= 1} onClick={() => setPageNum(pageNum - 1)}>Previous</button>
            <span>Page {pageNum} of {pdf?.numPages || 0}</span>
            <button disabled={!pdf || pageNum >= pdf.numPages} onClick={() => setPageNum(pageNum + 1)}>Next</button>
          </div>
          <button className="save-button" onClick={saveToBackend}>Save Field Mappings</button>
          <div className="fields-preview">
            <h3>Mapped Fields ({boxes.length})</h3>
            <pre>{JSON.stringify(boxes, null, 2)}</pre>
          </div>
        </>
      )}
    </div>
  );
}
