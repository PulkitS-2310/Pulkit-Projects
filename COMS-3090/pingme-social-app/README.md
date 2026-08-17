# PingMe Social Media App

PingMe is a COM S 3090 team project that includes an Android frontend and a Spring Boot backend for a social media-style application.

## Project Structure

```text
pingme-social-app/
├── frontend/   # Android Studio project
└── backend/    # Spring Boot backend project
```

## Frontend

The frontend is an Android application built with Java and Android Studio.

Main features include:
- Media posting and image upload screens
- Friends, search, profile, and settings pages
- Notifications and activity updates
- WebSocket-based real-time interaction
- Backend API integration using Volley/Postman-tested endpoints

## Backend

The backend is a Spring Boot project that supports user, post, chat, image, notification, analytics, and search functionality.

## Important Security Note

The original database credentials were removed before uploading this project to GitHub. Use `backend/src/main/resources/application-example.properties` as a template and create your own local `application.properties` when running the backend.

## Technologies Used

- Java
- Android Studio
- Spring Boot
- Maven
- WebSockets
- MySQL
- Volley
- Firebase Messaging

## What I Worked On

- Built and connected Android frontend screens
- Worked on user activity, notifications, friends, posting, and API-connected app flows
- Tested backend request flows and supported integration between frontend and backend features
