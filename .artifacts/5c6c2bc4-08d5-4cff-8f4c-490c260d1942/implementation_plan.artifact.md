# Fix Unresolved Reference 'icons' in HomeScreen.kt

The project is failing to build because the `androidx.compose.material.icons` package is not found. This package is part of the Compose Material Icons libraries, which are currently missing from the project's dependencies.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Renan/AndroidStudioProjects/VinylCollection/gradle/libs.versions.toml)
- Add `androidx-compose-material-icons-core` and `androidx-compose-material-icons-extended` to the `[libraries]` section.

#### [MODIFY] [build.gradle.kts (app)](file:///C:/Users/Renan/AndroidStudioProjects/VinylCollection/app/build.gradle.kts)
- Add `libs.androidx.compose.material.icons.core` and `libs.androidx.compose.material.icons.extended` to the `dependencies` block.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:compileDebugKotlin` to verify that the build error is resolved.

### Manual Verification
- Sync the project with Gradle files in Android Studio.
- Verify that `HomeScreen.kt` no longer shows unresolved reference errors for `Icons` and `Icons.Default.Add`.
