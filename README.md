# GameVault

A modern Android application for browsing and discovering video games by genre, built with Clean Architecture, MVVM, Jetpack Compose, and Material 3.
---

## Screenshots

### Games List
<p align="center">
  <img src="https://github.com/user-attachments/assets/e4f249a7-fac6-4a06-8361-ca8d5e80892a" width="250" alt="Games List Light"/>
  &nbsp;&nbsp;&nbsp;
  <img src="https://github.com/user-attachments/assets/1466e9bd-da5d-4529-b171-e04be56d59ff" width="250" alt="Games List Dark"/>
</p>
<p align="center">
  <b>Light Mode</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <b>Dark Mode</b>
</p>

### Game Details
<p align="center">
  <img src="https://github.com/user-attachments/assets/8d0af3eb-1334-4b20-beaa-f0c8adfbb2f4" width="250" alt="Game Details Light"/>
  &nbsp;&nbsp;&nbsp;
  <img src="https://github.com/user-attachments/assets/2e220797-88f4-488b-bc82-2b04e0b4bacb" width="250" alt="Game Details Dark"/>
</p>
<p align="center">
  <b>Light Mode</b> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <b>Dark Mode</b>
</p>

## Steps to Run the Project

**Important:** The app requires a RAWG API key to load any data. Without it, all API calls will fail and no games will be displayed.

1. Clone the repository
2. Open the project in Android Studio
3. Open the `local.properties` file in the project root directory
4. Add your RAWG API key:
```properties
   RAWG_API_KEY=your_api_key_here
```
5. If you don't have a key, get one for free at [rawg.io/apidocs](https://rawg.io/apidocs)
6. Sync Gradle and run the project

The `build.gradle.kts` file reads the key from `local.properties` automatically through `BuildConfig`. No additional configuration is needed.

Note: `local.properties` is gitignored by default so the API key is not pushed to the repository.

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Kotlin | Primary language |
| Jetpack Compose | Declarative UI framework |
| Material 3 | Design system with dark and light theming |
| Hilt | Dependency injection |
| Retrofit + OkHttp | Networking |
| Room | Local database for offline caching |
| Coroutines + StateFlow | Asynchronous operations and reactive state |
| Coil | Image loading |
| Navigation Compose 2.8+ | Type-safe navigation with Kotlin Serialization |
| Lottie | Splash screen animation |
| MockK + JUnit + Hamcrest | Unit testing |

---

## Architecture

The app follows **Clean Architecture** with **MVVM** pattern, separated into three layers:

- **Presentation** — Jetpack Compose screens, ViewModels, UiStates, navigation, and theme
- **Domain** — Business models, repository interface, and UseCases
- **Data** — Retrofit API service, Room database, DTOs, entity mappers, and repository implementation

Dependency injection is handled by **Hilt** with two modules: `AppModule` (Retrofit, Room, NetworkObserver) and `RepositoryModule` (repository binding).

---

## Features

**Screen 1 — Games List**
- Genre chips fetched dynamically from API with horizontal scrolling
- Game cards displaying cover image, name, rating, and genre tags
- Pagination with automatic next-page loading on scroll
- Local in-memory search filtering without additional API calls
- Shimmer loading placeholders during initial load

**Screen 2 — Game Details**
- Hero image with gradient overlay and back navigation
- Star rating, release date, and Metacritic score with Material Icons
- Expandable description section
- Genre chips
- Horizontal screenshots gallery
- Trailer card that opens in external browser

**Splash Screen**
- Lottie animation with fade-in app title
- Adapts to dark and light mode
- Auto-navigates after 1.5 seconds

**Dark and Light Mode**
- Full Material 3 theming across all screens
- Automatically follows system theme

**State Handling**
- Loading state with shimmer placeholders and pagination spinner
- Error state with retry button
- Empty state for no games and no search results
- Offline state with real-time network banner

---

## Bonus Features

### Offline Caching (Room Database)

The app implements a **network-first caching strategy** using Room. All three data types are cached locally:

| Data | Room Table | Behavior |
|---|---|---|
| Genres | `genres` | Cached on fetch, loaded from Room when offline |
| Games | `games` | Cached per genre and page, cleared on refresh |
| Game Details | `game_details` | Cached with screenshots and trailer URL |

When the device is online, data is fetched from the API and saved to Room. When offline, the app falls back to the cached data automatically.

### Network Status Indicator

Real-time connectivity monitoring using `ConnectivityManager` with Kotlin `callbackFlow`:

- Red banner appears instantly when the device goes offline: "You're offline — showing cached data"
- Green banner appears when connectivity is restored: "Back online" (auto-dismisses after 3 seconds)
- Animated with `AnimatedVisibility` on both screens

### Unit Tests (17 Tests)

Tests are written using **MockK** with `@MockK` annotations, **Coroutines Test**, and **Hamcrest** matchers.

| Test File | Tests | Coverage |
|---|---|---|
| `GetGenresUseCaseTest` | 2 | Success and failure paths |
| `GetGamesByGenreUseCaseTest` | 3 | Success, empty list, failure |
| `GetGameDetailsUseCaseTest` | 2 | Success and failure paths |
| `GamesListViewModelTest` | 9 | Loading, genres, games, search, pagination, error, retry |
| `GameDetailsViewModelTest` | 7 | Loading, success, error, retry, null metacritic, empty screenshots, null trailer |


### Additional API Endpoints

- `GET /games/{id}/screenshots` — Screenshots displayed in a horizontal gallery
- `GET /games/{id}/movies` — Trailer URL opened in external browser

### UI Polish

- Material 3 with custom color schemes for dark and light mode
- Shimmer loading animation
- All strings extracted to `strings.xml` for localization readiness
- Content descriptions on all interactive elements for accessibility
- Type-safe navigation using Kotlin Serialization
- Edge-to-edge display
- Custom app icon and Lottie splash screen

---

### Endpoints Used

| Endpoint | Purpose |
|---|---|
| `GET /genres` | Fetch all game genres |
| `GET /games?genres={slug}&page={n}` | Fetch games by genre, paginated |
| `GET /games/{id}` | Fetch game details |
| `GET /games/{id}/screenshots` | Fetch game screenshots |
| `GET /games/{id}/movies` | Fetch game trailers |

---

## Assumptions

- Genre list is fetched dynamically from the RAWG API, not hardcoded
- First genre is auto-selected on app launch
- Local search filters already-loaded games in-memory without making an API call
- Offline caching uses a network-first strategy with Room as fallback
- Game details screen combines data from three endpoints: details, screenshots, and trailers

---

## Requirements Checklist

| Requirement | Status |
|---|---|
| Genres list with selection | Done |
| Games list with name, image, rating | Done |
| Game details screen | Done |
| Basic pagination | Done |
| Local search (in-memory) | Done |
| Loading state | Done |
| Error state with retry | Done |
| Empty state | Done |
| Clean Architecture + MVVM | Done |
| Dependency Injection (Hilt) | Done |
| Dark and Light Mode | Done |
| Splash Screen (Lottie) | Done |
| **Bonus:** Offline caching (Room) | Done |
| **Bonus:** Unit tests (17 tests) | Done |
| **Bonus:** Network status indicator | Done |
| **Bonus:** Screenshots endpoint | Done |
| **Bonus:** Trailers endpoint | Done |
| **Bonus:** Material 3 UI polish | Done |
