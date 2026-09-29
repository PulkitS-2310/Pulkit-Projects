# PS_16
## Team
- Mokshitha Iragamreddy (mokshi28@iastate.edu)
- Pulkit Saxena (Pulkit@iastate.edu)

## Project Description
Closet Manager is a full-stack web application for organizing clothing items by category, color, size, and description. Users can view a dashboard, create and
delete categories, and create and delete closet items. The backend stores data in
a local JSON file so changes persist between server restarts.

## Tech Stack
- Frontend: React, Vite, JavaScript, CSS
- Backend: Node.js HTTP server
- Storage: JSON file storage in backend/data/closet.json

## Setup Instructions
1. Start the backend:
bash
cd backend
npm run dev
2. In a second terminal, start the frontend:
bash
cd frontend
npm install
npm run dev
3. Open the Vite URL shown in the frontend terminal, usually
 http://localhost:5173.
 
## Known Limitations
- The app uses local JSON file storage instead of a hosted database.
- Categories and items support create, read, and delete operations; edit forms
 are not included.
- The frontend expects the backend API to run on http://localhost:5000.