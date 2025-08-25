# NucorAlb – Anti-Corruption Android App

NucorAlb (short for No Corruption Albania) is a native Android application that provides a hub for sharing corruption‑related stories, forum posts, wiki articles and details about institutions. The repository contains the complete Android Studio project for the app.

## What the app does

The application opens with a welcome activity that shows a login/registration screen, an informational ViewFlipper for new users and a home feed. Once signed in, users can browse and contribute content via a button menu.

Key parts of the app include:

- **Home feed** – displays the latest stories, wiki articles, forum posts and institutions. The number of each item shown on the home screen is configurable through variables such as `numberOfStories`, `numberOfWikis`, `numberOfPosts` and `numberOfInstitutions`.

- **Forum** – lists discussion threads and allows users to start new threads or open existing ones. Threads are loaded asynchronously and displayed in a list; clicking the first entry opens a post editor while clicking others opens the full thread in a pop‑up activity. Forum posts, stories, wiki articles and institution pages are opened through a common `openPopUpActivity` method.

- **Wiki articles** – offline information about topics such as fraud and traffic fines is stored in `res/raw` and displayed to the user. For example, `fraud_wiki` contains a detailed definition and discussion of fraud, while `traffic_fines_wiki` lists typical fines and penalties.

- **Stories and institutions** – users can add their own stories or institutions through dedicated activities. The button menu in the toolbar provides options to add a story, add an institution, add a wiki article, share the app or log out. Institutions can be rated and displayed in list or detail views (see `InstitutionsActivity`, `AddInstitutionActivity` and `InstitutionPopUpActivity`).

- **Universal helper class** – `UniversalMethodsAndVariables` centralises common methods and shared variables so that activities can reuse behaviour without duplicating code. It contains utility methods for formatting dates, handling images, launching activities, calculating survey percentages and updating the button menu.

- **Search** – the toolbar contains a `SearchView` that filters content across the home feed and forum. When activated, the search bar animates into view and populates the results list through asynchronous database calls.

- **Database layer** – `DatabaseHelper` manages a local SQLite database containing tables for posts, stories, wiki articles, institutions and user data. On first launch the app calls `addPreliminaryData()` from `MainActivity` to seed the database with sample content.

- **OpenGL sample** – the project includes `Square.java`, `Triangle.java` and `MyGLRenderer.java`, which render simple shapes on an OpenGL `GLSurfaceView`. These files are derived from Android’s OpenGL ES samples and illustrate how to draw a rotating triangle; they are not used in the main app flow but serve as a reference.

## Project structure

```
NucorAlb/
├── build.gradle             # Top‑level Gradle file
├── app/
│   ├── build.gradle         # Module Gradle file specifying applicationId, SDK versions and dependencies
│   ├── proguard-rules.pro   # ProGuard configuration
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/anticorruption/  # Activity classes, adapters and helpers (ForumActivity, MainActivity, etc.)
│   │   │   ├── res/
│   │   │   │   ├── layout/   # XML layouts for activities, dialogs and list items
│   │   │   │   ├── drawable/ # Icons, logos and custom graphics
│   │   │   │   ├── raw/      # Offline wiki content (fraud_wiki, traffic_fines_wiki, etc.)
│   │   │   │   └── menu/     # Menu resource definitions
│   │   ├── androidTest/      # Instrumented UI tests
│   │   └── test/             # (optional) Unit tests
│   └── libs/                 # Third‑party JARs (e.g., Beanshell, Apache)
└── ...
```

## Build and installation

### Prerequisites

Install Android Studio (version 2.0 or later recommended) and ensure that you have Java Development Kit (JDK 7 or 8) installed. The project targets Android API 23 and requires a minimum API 17 device. Internet connectivity is needed for downloading dependencies such as the Android Support libraries, Facebook SDK and Google Play Services.

### Clone the repository

```
git clone https://github.com/awlondon/NucorAlb.git
cd NucorAlb
```

### Open in Android Studio

Choose **File → Open** in Android Studio and select the root directory of the cloned project. Let Gradle sync the project; this will download dependencies specified in `app/build.gradle`.

### Run the app

Connect an Android device (with Developer Options enabled) or start an emulator. Press the **Run** button in Android Studio to build and deploy the app. Alternatively, build from the command line:

```
./gradlew assembleDebug
```

The resulting APK will be located under `app/build/outputs/apk/`.

## Usage tips

- When the app first launches, the user is prompted to log in or register. New users can swipe through the information `ViewFlipper` to learn about the app’s purpose and controls.

- Use the button menu on the home screen to add stories, institutions or wiki articles, share the app with others or log out.

- Tap on items in the home feed to view details. The search icon in the toolbar filters stories, forum posts and institutions across the database.

- In the forum screen, select "Start a new thread +" to create a new discussion, or tap an existing thread to view or reply.

## Contributing

This repository appears to be a student or hackathon project and does not currently contain contribution guidelines or a license file. If you wish to contribute, fork the project on GitHub and open a pull request with your improvements. For questions about usage or extending the app, contact the repository owner through GitHub.

## License

The source code includes code licensed under the Apache License 2.0 from Android’s official samples (e.g., OpenGL classes). However, no explicit license is provided for the project as a whole. Treat this code as proprietary unless the authors supply a license.

