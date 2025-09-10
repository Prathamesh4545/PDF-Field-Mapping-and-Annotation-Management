# PDF-Field-Mapping-and-Annotation-Management

A full-stack application for mapping and annotating fields in PDF documents.

## Features

- Upload PDF files
- Interactive field mapping with mouse drawing
- Support for different field types (text, date, checkbox, signature)
- Save field mappings to database
- Multi-page PDF support

## Tech Stack

### Frontend
- React 19
- Vite
- PDF.js for PDF rendering
- Axios for API calls

### Backend
- Spring Boot 3.5.5
- Spring Data JPA
- PostgreSQL
- Lombok

## Setup Instructions

### Prerequisites
- Node.js 18+
- Java 21
- PostgreSQL 12+

### Backend Setup

1. Navigate to backend directory:
```bash
cd backend
```

2. Configure PostgreSQL in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=your_password
```

3. Run the application:
```bash
./mvnw spring-boot:run
```

Backend will start on http://localhost:8080

### Frontend Setup

1. Navigate to frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start development server:
```bash
npm run dev
```

Frontend will start on http://localhost:5173

## Usage

1. Open the application in your browser
2. The sample PDF will load automatically
3. Click and drag to create field mappings
4. Enter field name and type when prompted
5. Navigate between pages using Previous/Next buttons
6. Click "Save Field Mappings" to persist to database

## API Endpoints

- `POST /api/pdf/upload` - Upload PDF file
- `POST /api/pdf/{pdfId}/fields` - Save field mappings
- `GET /api/pdf/{pdfId}/fields` - Get field mappings

## Database Schema

### pdf_files
- id (Primary Key)
- file_name
- file_path
- upload_time

### pdf_fields
- id (Primary Key)
- field_name
- field_type
- page
- x1, y1, x2, y2 (coordinates)
- pdf_file_id (Foreign Key)
