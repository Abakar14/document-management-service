# document-management-service (DMS)
- This Service use for Centralized Document Management
- All Services should use this DMS for uploading and downloading all documents
- DMS can be reused accross multiple Services
- A DMS can handle authentication und authorization for file access Centrally, allowing only users with roles 
- and authority to access the documents
- This DMS handles storage in the file system or cloud storage (e.g Google Cloud, ....etc)
## Flow 
### Upload Document 
- Service send request to DMS for uploading a document (picture, certificates ....)
- The DMS store the document and return a **file ID** or URL to the service 
- Service take the **file ID** and store it in its database
### Download Document
- Service send request with **File ID or URL**  to DMS for downloading a document (picture, certificates ....)
