# 🎲 S&D — Schedule and Dice

<p align="center">
  <img src= "https://github.com/user-attachments/assets/3b2e68e8-c741-4dfd-9485-27186a50398c" alt="S&D Hero Banner" width="80%" />
</p>

<p align="center">
  <strong>Effortless scheduling for tabletop RPG groups.</strong><br/>
  Coordinate sessions, compare availability, and find the best dates — fast.
</p>

---

## ✨ Overview

**S&R — Schedule and Role** is an Android application built for tabletop RPG groups (such as Dungeons & Dragons) to simplify session planning.

Instead of endless group chats and polls, participants submit their availability, and S&D automatically highlights the best dates based on group attendance.

---

## 🚀 Features

### 📅 Event Management
- Create events with a unique 6-character code  
- Join events instantly using a shared code  
- Delete events (creator only)  

### 👥 Availability Coordination
- Submit and update your available dates anytime  
- Real-time shared availability grid  
- Live updates across all participants  

### 📊 Smart Recommendations

Each date is automatically categorized based on attendance:

| Tier | Threshold | Indicator |
|------|----------|----------|
| Full | ≥ 86% | 🟢 Excellent |
| Viable | ≥ 71% | 🟡 Good |
| Limited | ≥ 57% | 🟠 Possible |
| Insufficient | < 57% | 🔴 Unlikely |

- Recommended dates ranked by participation  
- Clear visibility of who cannot attend  

### 🔔 Notifications & Convenience
- Push notifications when availability changes  
- Recent events history for quick access  
- Easy code sharing via system share sheet  

---

## 🧱 Tech Stack

| Layer | Technology |
|------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Architecture | MVVM + Repository Pattern |
| Dependency Injection | Hilt |
| Backend | Firebase Cloud Firestore |
| Authentication | Firebase Anonymous Auth |
| Notifications | Firebase Cloud Messaging |
| Local Storage | SharedPreferences |
| Build System | Gradle (Version Catalog) |
| Min SDK | 29 |
| Target SDK | 35 |

---

