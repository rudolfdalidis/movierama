# MovieRama 🎬

MovieRama is a simple movie-sharing application where users can submit movies and vote (like or hate) on movies submitted by others. Users cannot vote on their own movies.

---

## Tech Stack

- Java 17, Spring Boot
- Spring Security (JWT, stateless)
- Spring Data JPA / Hibernate
- PostgreSQL (dev) / H2 (local)
- React + Vite
- Docker (PostgreSQL)

---

## Features

- User signup & login
- JWT authentication
- Create movies (authenticated users)
- List movies (public)
- Sort movies by date, likes, or hates
- Filter movies by submitting user
- Like / hate movies
- Switch or retract votes
- Prevent voting on own movies

---

## Running the Project

### Backend (H2 local)

```bash
cd backend
./mvnw spring-boot:run
```

### Backend (PostgreSQL via Docker)

```bash
docker-compose up -d
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs on http://localhost:5173, backend on http://localhost:8080.

---

## API (main endpoints)

- POST /api/auth/signup
- POST /api/auth/login
- GET  /api/me
- GET  /api/movies
- POST /api/movies
- PUT  /api/movies/{id}/vote

---

## Notes

- Usernames are case-sensitive
- Username must be at least 3 characters, password at least 8 characters
- Voting is limited to one vote per user per movie (DB-enforced)
- JWT authentication is fully stateless
