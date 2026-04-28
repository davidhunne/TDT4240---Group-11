# Mountain Penguin

Mountain Penguin is a multiplayer game with:

- a **Node.js/TypeScript backend** (Express + Firestore via Firebase Admin)
- a **Java/libGDX frontend** (core game logic + LWJGL3 desktop launcher + Android launcher)

## User Manual

### 3.1 Device Requirements

Android version:
- Android device/emulator with OpenGL ES 2.0 support
- Internet access
- Reachable backend server

Desktop development version:
- Java and Gradle support (using the included Gradle wrapper)
- Network access to backend server
- Firebase service configuration available to backend

Backend:
- Node.js installed

### 3.2 Installation

Backend setup:
1. `cd backend`
2. `npm install`
3. Configure `FIREBASE_PRIVATE_KEY`, `FIREBASE_PROJECT_ID`, `FIREBASE_CLIENT_EMAIL`
4. Start with `npm run dev` (development) or `npm run build` then `npm start` (compiled run)
5. Confirm backend health at `/health`

Production backend URL: `https://tdt-4240.vercel.app`

Frontend setup:
1. Open `frontend/` in Android Studio (or another Gradle-compatible IDE)
2. Configure Android string resource `api_base_url` in `frontend/android/res/values/strings.xml` so it points to your backend API (for emulator use `http://10.0.2.2:3000/api`; for production use `https://tdt-4240.vercel.app/api`)
3. Build and run the `android` target on an emulator or device

Optional desktop testing:
- Run the `lwjgl3:run` Gradle task

### 3.3 How to Play

1. Start the game
2. Enter a player name in the main menu
3. Create a lobby, or enter an existing lobby code and join it
4. Wait for other players to join
5. If you are host, press **Start Game** once enough players are present
6. In desktop game state, press **Space** to end the game and open the results screen
7. Use the results screen to return to the main menu
8. Use **Settings** from the main menu to adjust basic volume

## About the game

Mountain Penguin is a turn-based multiplayer game where each player controls a penguin on a shared board.
Players prepare turns making moves while the backend validates movement, collisions, and board interactions such as obstacles and boosts.
Lobbies, active matches, and player statistics are synchronized through the backend so all clients see a consistent game state.
The goal is to outplay opponents across rounds and reach the end of the map first.

## Workspace structure

```text
.
├── backend/   # TypeScript API server
└── frontend/  # libGDX game project (core, lwjgl3, android)
```

## Tech stack

| Area | Stack |
| --- | --- |
| Backend | TypeScript, Express 5, Zod, Firebase Admin SDK (Firestore) |
| Frontend | Java 8-targeted libGDX 1.14, Gradle multi-project (`core`, `lwjgl3`, `android`) |

## Prerequisites

- Node.js + npm (for `backend/`)
- Java JDK (17 recommended for local tooling; frontend compiles for Java 8 target)
- Android SDK only if building/running `frontend/android/`

## Architecture overview

- **Client-server architecture**: the backend is authoritative for game state and rule enforcement.
- **ECS simulation on backend**: game simulation logic is implemented with an Entity Component System in `backend/src/ecs/`.
- **Thin client approach**: the frontend sends player actions and renders the server-provided state.
- **Firestore persistence**: player profiles, lobbies, games, and move history are stored in Firestore.

## Quick start

1. Start the backend:

```powershell
cd backend
npm install
npm run dev
```

2. Confirm backend health:

Open `http://localhost:3000/health` in a browser.

3. In a second terminal, start the desktop game:

```powershell
cd frontend
.\gradlew.bat lwjgl3:run
```

The backend defaults to `http://localhost:3000`, and the desktop client defaults to `http://localhost:3000/api`.

## Backend

Path: `backend`

### Environment variables (`backend/.env`)

Use the team-provided `backend/.env`.

If you need to create one locally, create a Firebase service account (Firebase Console -> Project Settings -> Service accounts), download the JSON key, and map values into `backend/.env`:

- `project_id` -> `FIREBASE_PROJECT_ID`
- `client_email` -> `FIREBASE_CLIENT_EMAIL`
- `private_key` -> `FIREBASE_PRIVATE_KEY`

Then create `backend/.env`:

```env
PORT=3000
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_CLIENT_EMAIL=your-service-account-email
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n"
```

The private key is read with escaped newlines (`\n`) and converted at runtime.

Do not commit service account files or `.env` secrets to Git.

### Backend scripts

- `npm run dev` - run server with `ts-node-dev`
- `npm run build` - compile TypeScript to `dist/`
- `npm start` - run compiled server

### API overview

- `GET /health`
- `POST /api/players`
- `GET /api/players/:id`
- `PATCH /api/players/:id`
- `GET /api/players/leaderboard/top?limit=10`
- `GET /api/games/lobbies`
- `POST /api/games`
- `GET /api/games/:gameId`
- `POST /api/games/:gameId/join`
- `POST /api/games/:gameId/leave`
- `POST /api/games/:gameId/start`
- `POST /api/games/:gameId/move`
- `POST /api/games/:gameId/end`
- `GET /api/games/:gameId/moves`
- `POST /api/games/:gameId/connection`

### Backend notes

- Request validation is done with Zod schemas in `backend/src/schemas.ts`.
- The game service maintains turn/order/board state and syncs ECS world state (`backend/src/ecs`) to Firestore.

## Frontend

Path: `frontend/`

### IDE setup

Open `frontend/` in Android Studio (or another Gradle-compatible IDE) to work with the Android target.

### Modules

- `core`: gameplay, state machine, networking, models/controllers
- `lwjgl3`: desktop launcher (`Lwjgl3Launcher`)
- `android`: Android launcher (`AndroidLauncher`)

### Frontend run/build commands

```powershell
cd frontend
.\gradlew.bat lwjgl3:run
```

- `.\gradlew.bat build` - build all frontend modules
- `.\gradlew.bat lwjgl3:jar` - build desktop runnable JAR
- `.\gradlew.bat android:assembleDebug` - build Android debug APK

### Backend URL configuration in clients

- Desktop uses `http://localhost:3000/api` by default.
- You can override desktop URL with JVM property:
  - `.\gradlew.bat lwjgl3:run -Dmtnpen.apiBaseUrl=http://<host>:3000/api`
- Android reads `api_base_url` from `frontend/android/res/values/strings.xml` (currently `http://10.0.2.2:3000/api` for emulator-to-host access).

### Android run flow

1. Set `api_base_url` in `frontend/android/res/values/strings.xml` to your backend API.
2. Build and run the `android` target on an emulator or device.
3. For desktop testing, run the `lwjgl3:run` Gradle task.

## Troubleshooting

- If Android cannot connect to backend:
  - Ensure backend is running.
  - Use `10.0.2.2` (not `localhost`) for emulator-to-host routing.
- If Gradle fails unexpectedly:
  - Ensure Java 17 is installed and active in your environment.
- If backend fails with missing `project_id` / Firebase credential errors:
  - Ensure `backend/.env` exists and contains `FIREBASE_PROJECT_ID`, `FIREBASE_CLIENT_EMAIL`, and `FIREBASE_PRIVATE_KEY`.
  - If startup logs show `injected env (0)`, no variables were loaded from `.env`.

## Production notes

- Backend can be built with `npm run build` in `backend/` and deployed as a standard Node.js service.
- Frontend desktop builds can be packaged with `.\gradlew.bat lwjgl3:jar` and distributed as a runnable JAR.
- Production backend URL: `https://tdt-4240.vercel.app`
