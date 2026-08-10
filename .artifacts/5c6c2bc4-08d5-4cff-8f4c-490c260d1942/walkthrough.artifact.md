# Walkthrough - Fixing Unresolved Material Icons

I have resolved the "Unresolved reference 'icons'" error by adding the necessary Material Icons dependencies to the project.

## Changes

### Dependencies

#### [libs.versions.toml](file:///C:/Users/Renan/AndroidStudioProjects/VinylCollection/gradle/libs.versions.toml)
Added `androidx-compose-material-icons-core` and `androidx-compose-material-icons-extended` to the version catalog.

#### [build.gradle.kts (app)](file:///C:/Users/Renan/AndroidStudioProjects/VinylCollection/app/build.gradle.kts)
Included the new library aliases in the `dependencies` block.

## Verification Results

### Automated Tests
- Ran `./gradlew :app:compileDebugKotlin`: **SUCCESS**
- Gradle Sync: **SUCCESS**

The `HomeScreen.kt` file now compiles correctly as the `Icons` symbol is properly resolved from the added dependencies.
