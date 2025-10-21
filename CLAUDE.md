# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SafezonePet is an Android pet tracking application built with Jetpack Compose and Kotlin. The app allows pet owners to track their pets using GPS trackers, report lost pets, create groups, view location history, and connect with a community of pet owners.

**Package Name:** `com.example.screens`
**Application ID:** `com.example.screens`
**Min SDK:** 29 (Android 10)
**Target SDK:** 36

## Build System

This project uses Gradle with Kotlin DSL (`.kts` files) and the Android Gradle Plugin (AGP) version 8.12.2.

### Building the Project

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean build
./gradlew clean

# Install debug build on connected device/emulator
./gradlew installDebug
```

### Running Tests

```bash
# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests ExampleUnitTest

# Run all tests with coverage
./gradlew testDebugUnitTest
```

## Architecture

### Navigation Architecture

The app uses **Jetpack Compose Navigation** with a centralized navigation graph defined in `navigation/Navigation.kt`. All navigation routes are defined as sealed classes in the `Screen` object:

- Navigation flows: Login → Signup → Loading → Map (main screen)
- Main screens accessible via bottom navigation: Map, Groups, Pet Profile, Community (Reports)
- Secondary screens: Settings, Notifications, Location History, Connect Tracker, Create Group, Group Detail

**Key Pattern:** Each screen has a `*WithNavigation` wrapper function that handles navigation callbacks and passes the NavController.

### Package Structure

```
com.example.screens/
├── activity/              # Legacy activities (EmailPasswordActivity)
├── apibackend/            # API integration layer (currently empty: dogs.kt, users.kt)
├── Data/                  # Data models and repositories
│   ├── AuthRepository.kt  # Firebase authentication logic
│   ├── Pet.kt            # Pet data model
│   ├── Group.kt          # Group data model
│   └── PermissionMessage.kt
├── footer/               # Bottom navigation bar
│   └── AppNavigationBar_2.kt
├── navigation/           # Navigation configuration
│   └── Navigation.kt     # App navigation graph and route definitions
├── permission/           # Permission handling dialogs
│   └── PermissionDialog.kt
├── ui/                   # All Compose UI screens
│   ├── auth/            # Authentication ViewModels
│   ├── components/      # Reusable UI components
│   ├── theme/           # App theming (Color, Type, Theme)
│   └── [Screen files]   # Individual screen composables
└── MainActivity.kt       # App entry point
```

### State Management

- **ViewModels:** Used for authentication logic (`ui/auth/AuthViewModel.kt`)
- **Repository Pattern:** `AuthRepository.kt` wraps Firebase authentication with Kotlin coroutines
- **Compose State:** Local state management using `mutableStateOf` and `remember`

### Authentication

Firebase Authentication is used for user login/signup:
- `AuthRepository` provides suspend functions for `signIn()` and `signUp()`
- `AuthViewModel` manages authentication state and loading/error states
- Navigation flows from Login/Signup → Loading → Map on successful authentication

## Key Dependencies

- **Jetpack Compose:** UI framework (Material3, BOM version 2024.09.00)
- **Navigation Compose:** 2.8.5
- **Firebase:** Authentication (with BOM 34.4.0)
- **Coil:** 2.6.0 for image loading
- **Kotlin Coroutines:** For async operations with Firebase

## Firebase Configuration

The project requires `google-services.json` in the `app/` directory for Firebase integration. This file contains Firebase project configuration and should not be committed to version control.

## Permissions

The app requests the following permissions (defined in `AndroidManifest.xml`):
- `INTERNET` - Network access
- `CAMERA` - Taking photos of pets
- `READ_EXTERNAL_STORAGE` / `READ_MEDIA_IMAGES` - Accessing pet photos
- `WRITE_EXTERNAL_STORAGE` (SDK ≤28) - Saving photos

## Important Development Notes

### Adding New Screens

1. Create composable function in `ui/` directory (e.g., `NewScreen.kt`)
2. Create `NewScreenWithNavigation()` wrapper that accepts `navController` and callback parameters
3. Add route to `Screen` sealed class in `Navigation.kt`
4. Add `composable()` entry in `AppNavigation()` function
5. If the screen should be in bottom navigation, update `AppNavigationBar_2.kt`

### Working with Navigation

- Use `navController.navigate(Screen.RouteName.route)` to navigate forward
- Use `navController.popBackStack()` to go back
- Use `popUpTo` with `inclusive = true` to clear backstack (e.g., after login)
- Use `launchSingleTop = true` and `restoreState = true` for bottom navigation items

### Theme and Styling

- Custom colors defined in `ui/theme/Color.kt` (includes `PetSafeGreen`, `TextWhite`)
- Typography in `ui/theme/Type.kt`
- Theme application in `ui/theme/Theme.kt`
- Material3 is used throughout the app

### Backend Integration (Future Work)

The `apibackend/` package contains placeholder files (`dogs.kt`, `users.kt`) for future REST API integration. When implementing:
- Define data classes for API requests/responses
- Use Retrofit or similar HTTP client
- Follow repository pattern similar to `AuthRepository`

## Common Patterns

### Screen Composable Pattern
```kotlin
@Composable
fun MyScreenWithNavigation(
    navController: NavController,
    onBackClick: () -> Unit,
    onSomeAction: () -> Unit
) {
    MyScreen(
        onBackClick = onBackClick,
        onSomeAction = onSomeAction
    )
}

@Composable
fun MyScreen(
    onBackClick: () -> Unit,
    onSomeAction: () -> Unit
) {
    // Screen implementation
}
```

### Navigation with Parameters
```kotlin
// Define route with parameter
object MyScreen : Screen("my_screen/{param}") {
    fun createRoute(param: String) = "my_screen/$param"
}

// Navigate
navController.navigate(Screen.MyScreen.createRoute("value"))

// Receive parameter
composable(
    route = Screen.MyScreen.route,
    arguments = listOf(navArgument("param") { type = NavType.StringType })
) { backStackEntry ->
    val param = backStackEntry.arguments?.getString("param") ?: ""
    MyScreenWithNavigation(param = param)
}
```

## File Provider Configuration

For camera functionality, the app uses `FileProvider` configured in `AndroidManifest.xml` with paths defined in `res/xml/paths.xml`.
