# document-management-service (DMS)
- This Service use for Centralized Document Management and handles reports using JasperReports
- Store generated reports (pdf, excel, etc) and save it in file system or cloud storage (like AWS S3)
- Expose APIs to generate and download reports based on data received from other services (e.g. BFF)
- Stores report remplates(JRXML files) and uses them to generate reports dynamically.
- All Services should use this DMS for uploading and downloading all documents
- DMS can be reused accross multiple Services
- A DMS can handle authentication und authorization for file access Centrally, allowing only users with roles 
- and authority to access the documents
- This DMS handles storage in the file system or cloud storage (e.g Google Cloud, ....etc)
## Interaction Flows 
### Upload Document 
- Service send request to DMS for uploading a document (picture, certificates ....)
- The DMS store the document and return a **file ID** or URL to the service 
- Service take the **file ID** and store it in its database
### Download Document
- Service send request with **File ID or URL**  to DMS for downloading a document (picture, certificates ....)
### Reports
1. Frontend requests a report (e.g., a PDF or Excel) via the BFF service.
2. BFF calls the Student and/or Teacher service to fetch relevant data.
3. BFF forwards the data to the Document Management Service (DMS) along with the report format (PDF, Excel, etc.).
4. DMS generates the report using JasperReports and stores the file.
5. BFF provides the download link or returns the report file to the Frontend.