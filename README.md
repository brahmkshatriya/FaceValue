# FaceValue

An android only application that detects faces in images and allows tagging them with a face.
It uses Google's MediaPipe library to detect faces in images.

## Features

- Clean architecture with MVVM pattern and Single source
  of [Dependency Injection](./app/src/main/java/dev/brahmkshatriya/facevalue/DI.kt).
- Nice Material You UI in [Android Views](./app/src/main/java/dev/brahmkshatriya/facevalue/ui).
- Face Detection Model is downloaded and cached with the [Gradle Script](./app/build.gradle.kts).
- [Permissions](./app/src/main/java/dev/brahmkshatriya/facevalue/utils/PermsUtils.kt) are requested
  at runtime.
- Supports
  multiple [Image Repositories](./app/src/main/java/dev/brahmkshatriya/facevalue/data/repos).
    - [Local](./app/src/main/java/dev/brahmkshatriya/facevalue/data/repos/local) Image Repository.
    - [Pixabay](./app/src/main/java/dev/brahmkshatriya/facevalue/data/repos/pixabay) Image
      Repository (Remote).
- Image data is cached in the [Database](./app/src/main/java/dev/brahmkshatriya/facevalue/data/db).
- Uses WorkManager
  to [load images](./app/src/main/java/dev/brahmkshatriya/facevalue/data/worker/LoadImagesWorker.kt)
  and [detect faces](./app/src/main/java/dev/brahmkshatriya/facevalue/data/worker/DetectFacesWorker.kt)
  in the background.
- Allows creating and
  reusing [Faces](./app/src/main/java/dev/brahmkshatriya/facevalue/data/db/FaceDao.kt) for tagging.

## App Flow

### Face Detection Flow

![image](https://github.com/user-attachments/assets/acda4246-62af-4ba6-ad3c-ce658858552a)


## Building

### Prerequisites

To build and run the app, you need to have the following installed:

- Android Studio Meerkat(2024.3.1) or later
- Java 17 or later
- Android device or emulator running Android 7.0 or higher

### Steps to build

1. Clone this repository to your local machine.
2. Open the project in Android Studio and wait for the Gradle build to finish.
3. Connect your Android device or start an emulator.
4. Run the app from Android Studio by clicking the "Run" button or using the shortcut `Shift + F10`.
