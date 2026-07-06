# Deployment Guide

## Option 1: Docker Compose

Requirements:

- Docker Desktop
- Optional AI key in `OPENAI_API_KEY`

Commands:

```bash
cp .env.example .env
docker compose up --build
```

Open:

- Frontend: `http://localhost:3000`
- Backend health: `http://localhost:8080/api/health`

## Option 2: Separate Services

Deploy the backend as a Java web service:

```bash
cd backend
mvn -DskipTests package
java -jar target/ai-quiz-builder-0.0.1-SNAPSHOT.jar
```

Environment variables:

```text
OPENAI_API_KEY
OPENAI_MODEL
OPENAI_BASE_URL
```

Deploy the frontend as a static site:

```bash
cd frontend
pnpm install
pnpm run build
```

Publish the `frontend/dist` folder. Set `VITE_API_BASE` during build when the API is on a different host:

```bash
VITE_API_BASE=https://your-backend.example.com pnpm run build
```

## Platform Notes

- Render/Railway/Fly.io: deploy `backend/Dockerfile` as the API service and `frontend/Dockerfile` as the web service.
- Netlify/Vercel frontend: build command `pnpm run build`, publish directory `dist`, env `VITE_API_BASE=https://your-api-host`.
- Any Spring Boot host: use Java 17 or newer.

