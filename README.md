# Schednd

**Schednd** is an Android application designed to help tabletop RPG groups — particularly D&D sessions — coordinate and agree on the best meeting dates. Participants submit their availability, and the app surfaces the dates with the highest group attendance, making scheduling effortless.

---

## Features

- **Create events** — Generate a unique 6-character code to share with your group.
- **Join events** — Enter a code to access an existing event and submit your available dates.
- **Real-time availability grid** — See all participants' availability at a glance as it updates live.
- **Attendance tiers** — Each date is automatically classified by the percentage of participants available:

  | Tier | Threshold | Color | Icon |
  |------|-----------|-------|------|
  | Full | ≥ 86% | 🟢 Green | Check Circle |
  | Viable | ≥ 71% | 🟡 Yellow | Thumb Up |
  | Limited | ≥ 57% | 🟠 Orange | Thumb Down |
  | Insufficient | < 57% | 🔴 Red | Cancel |

- **Recommended dates card** — A summary card lists the best dates with absent participant names and attendance counts.
- **Edit availability** — Any participant (including the event creator) can update their submitted dates at any time.
- **Delete event** — The event creator can permanently delete an event.
- **Recent events** — Quickly re-access events you have previously joined or created directly from the home screen.
- **Share event code** — Share the code via the system share sheet or copy it to the clipboard.
- **Push notifications** — Receive a notification when participants update their availability (via Firebase Cloud Messaging).

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 2.1.20 |
| UI | Jetpack Compose + Material3 |
| Architecture | MVVM + Repository pattern |
| Dependency injection | Hilt 2.53.1 |
| Database | Cloud Firestore (real-time) |
| Authentication | Firebase Anonymous Auth |
| Notifications | Firebase Cloud Messaging |
| Local storage | SharedPreferences |
| Build system | Gradle with Version Catalog (`libs.versions.toml`) |
| Min SDK | 29 (Android 10) |
| Target SDK | 35 (Android 15) |

---

## Project Structure

```
com.schednd
├── data
│   ├── repository
│   │   ├── AuthRepository.kt          # Anonymous Firebase Auth
│   │   ├── EventRepository.kt         # Firestore CRUD + real-time streams
│   │   ├── MessagingRepository.kt     # FCM topic subscriptions
│   │   └── RecentEventsRepository.kt  # SharedPreferences recent-events cache
│   ├── service
│   │   └── SchedndMessagingService.kt # FCM push notification handler
│   └── util
│       └── ShortCodeGenerator.kt      # 6-char alphanumeric code generator
├── di
│   └── AppModule.kt                   # Hilt module — Firebase + repository bindings
├── model
│   ├── Event.kt                       # Event domain model
│   └── Participant.kt                 # Participant domain model
└── ui
    ├── components
    │   ├── AvailabilityGrid.kt        # Participant × date availability matrix
    │   └── CalendarGrid.kt            # Interactive month calendar with attendee counts
    ├── create                         # Create-event screen + ViewModel
    ├── detail                         # Event-detail screen + ViewModel
    ├── home                           # Home screen + ViewModel
    ├── join                           # Join-event screen + ViewModel
    ├── navigation
    │   └── SchedndNavGraph.kt         # Compose navigation host
    └── theme                          # Material3 color, type, and theme definitions
```

---

## Screens

### Home
The entry point. Displays quick access to recently visited events and buttons to create or join a new event.

### Create Event
A two-phase flow:
1. Enter an event name and your display name.
2. After the event is created, select your own available dates on a calendar and save.

The generated 6-character code can be shared directly from this screen.

### Join Event
Enter a 6-character event code, choose a display name, and pick your available dates. The calendar shows how many other participants have already marked each date.

### Event Detail
The main coordination hub:
- A color-coded availability grid shows every participant's submitted dates.
- A summary card lists recommended dates in order of attendance, including the names of absent participants.
- Any participant can tap **Edit my availability** to update their dates inline.
- The event creator has an additional **Delete event** option in the top bar.

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- A Firebase project with **Firestore**, **Authentication**, and **Cloud Messaging** enabled
- `google-services.json` placed in `app/`

### Build

```bash
# Clone the repository
git clone https://github.com/your-username/Schednd.git

# Open in Android Studio and sync Gradle
# Or build from the command line:
./gradlew assembleDebug
```

### Firebase Setup

1. Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com).
2. Enable **Anonymous Authentication**.
3. Create a **Cloud Firestore** database in production mode with the following structure:

```
events/{code}
  └── participants/{userId}
```

4. Enable **Cloud Messaging**.
5. Download `google-services.json` and place it in `app/`.

---

## Firestore Data Model

```
events (collection)
└── {code} (document)
    ├── name:       String
    ├── code:       String
    ├── creatorId:  String
    ├── createdAt:  Timestamp
    └── participants (subcollection)
        └── {userId} (document)
            ├── userId:         String
            ├── name:           String
            └── availableDates: List<Timestamp>
```

---

## License

This project is for personal and educational use. No license has been applied yet.
