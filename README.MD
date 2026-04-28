# MountainPenguin

> **Important**
> To the examiner: Paste the environment variables that was in the deliverable. If you do not need to test the backend locally, you can leave the frontend API URL unchanged and run the frontend alone, it will then use the deployed Vercel backend. The game requires 2+ players in order to play.

MountainPenguin is split into two runnable parts:

- `backend/`: an Express + TypeScript API backed by Firebase/Firestore
- `frontend/`: a libGDX game client with shared core code plus desktop (`lwjgl3`) and Android launchers

The frontend is configured to use the deployed production API on Vercel by default. If you want to run the backend locally, you must update the frontend API base URL first.

## Prerequisites

### Required for the backend

- Node.js: use a current LTS release
- Firebase Admin credentials for the Firestore project used by the backend

Create `backend/.env` with:

```env
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_CLIENT_EMAIL==your-service-account-email
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n"
```

### Required for the frontend

- JDK 17 for Gradle and Android builds
- Gradle wrapper is included; no separate Gradle install is needed
- Android Studio + Android SDK if you want to run the Android client
- `ANDROID_SDK_ROOT` set, or `frontend/local.properties` containing:

```properties
sdk.dir=/absolute/path/to/your/Android/Sdk
```

## Project Structure

```text
.
├── backend/
│   ├── src/config/       # Firebase initialization
│   ├── src/routes/       # Express route definitions
│   ├── src/services/     # Game and player business logic
│   ├── src/ecs/          # ECS world and gameplay systems
│   ├── src/middleware/   # Error handling
│   └── src/index.ts      # Backend entrypoint
└── frontend/
    ├── assets/           # Shared game assets
    ├── core/             # Shared game logic, networking, states, MVC-style structure
    ├── lwjgl3/           # Desktop launcher
    ├── android/          # Android launcher and resources
    ├── build.gradle      # Root frontend Gradle build
    └── settings.gradle   # Gradle module declarations
```

Important frontend files for API configuration:

- `frontend/core/src/main/java/com/assignments/mtnpen/network/NetworkManager.java`
- `frontend/android/res/values/strings.xml`

## Running Locally

### 1. Install dependencies

Backend:

```bash
cd backend
npm install
```

Frontend:

```bash
cd frontend
./gradlew build
```

`./gradlew build` compiles all frontend modules. If you only want the desktop client, `./gradlew lwjgl3:build` is enough.

### 2. Point the frontend at the local backend

By default, the project targets the production deployment hosted on Vercel:

- Production host: `https://tdt-4240.vercel.app`
- Configured API base URL in code: `https://tdt-4240.vercel.app/api`
- Desktop uses `http://localhost:3000/api` by default.
- You can override desktop URL with JVM property:
  - `.\gradlew.bat lwjgl3:run -Dmtnpen.apiBaseUrl=http://<host>:3000/api`
- Android reads `api_base_url` from `frontend/android/res/values/strings.xml` (currently `http://10.0.2.2:3000/api` for emulator-to-host access).

If you want to run the backend locally, change the API base URL in both of these files:

- `frontend/android/res/values/strings.xml`
- `frontend/core/src/main/java/com/assignments/mtnpen/network/NetworkManager.java`

Change:

```text
https://tdt-4240.vercel.app/api
```

to:

```text
http://localhost:3000/api
```

Use `http://localhost:3000/api`, not just `http://localhost:3000`, because the backend mounts its routes under `/api`.

When you want to go back to production, restore the value to `https://tdt-4240.vercel.app/api`.

### 3. Start the backend

Development mode:

```bash
cd backend
npm run dev
```

Compiled execution:

```bash
cd backend
npm run build
npm start
```

Useful check:

- Health endpoint: `http://localhost:3000/health`

### 4. Start the frontend

Desktop client:

```bash
cd frontend
./gradlew lwjgl3:run
```

Android client:

```bash
cd frontend
./gradlew android:assembleDebug
./gradlew android:installDebug
./gradlew android:run
```

For Android development, opening `frontend/` in Android Studio also works.

## Compilation and Execution Reference

### Backend

- `npm run dev`: runs the API with `ts-node-dev`
- `npm run build`: compiles TypeScript into `backend/dist/`
- `npm start`: runs the compiled server from `dist/index.js`

### Frontend

- `./gradlew build`: builds all frontend modules
- `./gradlew lwjgl3:run`: runs the desktop game
- `./gradlew lwjgl3:jar`: creates a runnable desktop JAR in `frontend/lwjgl3/build/libs/`
- `./gradlew android:assembleDebug`: builds the Android debug APK
- `./gradlew android:installDebug`: installs the debug APK on a connected device/emulator
- `./gradlew android:run`: launches the Android app through `adb`

## Suggested Local Workflow

1. Start the backend from `backend/`.
2. Verify `http://localhost:3000/health` responds with `{"status":"ok"}`.
3. Update the two frontend API base URL files to `http://localhost:3000/api`.
4. Run the desktop client with `./gradlew lwjgl3:run` or launch the Android client.

If you do not need to test the backend locally, you can leave the frontend API URL unchanged and run the frontend against the deployed Vercel backend.

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

### How to Play

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

## Tech stack

| Area     | Stack                                                                           |
| -------- | ------------------------------------------------------------------------------- |
| Backend  | TypeScript, Express 5, Zod, Firebase Admin SDK (Firestore)                      |
| Frontend | Java 8-targeted libGDX 1.14, Gradle multi-project (`core`, `lwjgl3`, `android`) |

## Architecture overview

- **Client-server architecture**: the backend is authoritative for game state and rule enforcement.
- **ECS simulation on backend**: game simulation logic is implemented with an Entity Component System in `backend/src/ecs/`.
- **Thin client approach**: the frontend sends player actions and renders the server-provided state.
- **Firestore persistence**: player profiles, lobbies, games, and move history are stored in Firestore.
