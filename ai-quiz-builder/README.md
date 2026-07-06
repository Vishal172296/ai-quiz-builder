# AI Quiz Builder

Full-stack quiz generator built with Java 17, Spring Boot 3, and React.

## Features

- Generate multiple-choice quizzes from a topic, difficulty, and question count.
- Uses an OpenAI-compatible chat completion endpoint when `OPENAI_API_KEY` is configured.
- Falls back to a deterministic local generator when no AI key is available.
- React UI with answer reveal, scoring, and quiz export as JSON.
- Dockerfiles and `docker-compose.yml` for local deployment.

## Project Layout

```text
ai-quiz-builder/
  backend/     Spring Boot REST API
  frontend/    React + Vite client
```

## Run Locally

### Backend

```bash
cd backend
mvn spring-boot:run
```

On Windows PowerShell:

```powershell
cd backend
mvn spring-boot:run
```

Optional AI configuration:

```powershell
$env:OPENAI_API_KEY="your_api_key"
$env:OPENAI_MODEL="gpt-4o-mini"
```

The API runs at `http://localhost:8080`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The UI runs at `http://localhost:5173`.

## Deploy With Docker Compose

```bash
docker compose up --build
```

With AI enabled:

```bash
OPENAI_API_KEY=your_api_key docker compose up --build
```

Open `http://localhost:3000`.

## API

`POST /api/quizzes/generate`

Request:

```json
{
  "topic": "Java streams",
  "difficulty": "medium",
  "questionCount": 5
}
```

Response:

```json
{
  "title": "Java streams Quiz",
  "topic": "Java streams",
  "difficulty": "medium",
  "questions": [
    {
      "prompt": "Which statement best describes Java streams?",
      "options": ["...", "...", "...", "..."],
      "answerIndex": 0,
      "explanation": "..."
    }
  ]
}
```
