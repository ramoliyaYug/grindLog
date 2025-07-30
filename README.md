# 🚀 GrindLog - Daily Journal for DSA & Competitive Coders

GrindLog is your personal coding journal that helps you **track your DSA & CP progress**, **set daily targets**, **analyze performance**, and **stay consistent** — all in a clean and intuitive interface built with **Kotlin**, **Jetpack Compose**, and **RoomDB**.

> 💡 Designed for LeetCode, Codeforces, CodeChef, and GFG enthusiasts who want to take their grind seriously.

---

## 📸 Screenshots
<table>
  <tr>
    <th>Today Screen</th>
    <th>Reminder Screen</th>
    <th>Analysis Screen</th>
    <th>To-Do Screen</th>
    <th>Profile Screen</th>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/73dc2ca3-f349-45e7-8989-c9b1746b7b31" alt="Today Screen" width="200"/></td>
    <td><img src="https://github.com/user-attachments/assets/74d5abb6-b59a-4d01-89cc-a6931ae97565" alt="Reminder Screen" width="200"/></td>
    <td><img src="https://github.com/user-attachments/assets/45e9728b-70f0-445c-ac66-31fe2287fb9c" alt="Analysis Screen" width="200"/></td>
    <td><img src="https://github.com/user-attachments/assets/fe600966-7b57-4554-86b7-da5411120815" alt="To-Do Screen" width="200"/></td>
    <td><img src="https://github.com/user-attachments/assets/0798d949-6c19-4434-8af9-37a0a8d63a8d" alt="Profile Screen" width="200"/></td>
  </tr>
</table>

---
## ✨ Features

<table>
  <thead>
    <tr>
      <th>🗓️ Today Screen</th>
      <th>⏰ Reminder Screen</th>
      <th>📊 Analysis Screen</th>
      <th>✅ To-Do Screen</th>
      <th>👤 Profile Screen</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>
        <ul>
          <li>Log daily progress (LeetCode, Codeforces, CodeChef, GFG)</li>
          <li>Set daily targets for each platform</li>
          <li>View % of goal completion</li>
          <li>Add daily journal note on what you've learned</li>
        </ul>
      </td>
      <td>
        <ul>
          <li>Set reminders for upcoming contests</li>
          <li>Create custom event reminders</li>
          <li>Notifies 1 hour before via local notifications</li>
        </ul>
      </td>
      <td>
        <ul>
          <li>Visual analytics with filters (week/month/year)</li>
          <li>Platform-specific performance stats</li>
          <li>Specific Date Filter:
            <ul>
              <li>Platform activity</li>
              <li>Daily target achievement</li>
              <li>To-do completion</li>
            </ul>
          </li>
        </ul>
      </td>
      <td>
        <ul>
          <li>Create and manage daily to-dos</li>
          <li>Track completion progress</li>
          <li>View historical stats on productivity</li>
        </ul>
      </td>
      <td>
        <ul>
          <li>View your data and journal notes</li>
          <li>Theme toggle (light/dark)</li>
          <li>Delete or reset app data</li>
          <li>Filter notes by date/title</li>
        </ul>
      </td>
    </tr>
  </tbody>
</table>

---

## 🛠️ Tech Stack

| Layer | Tech |
|------|------|
| **Language** | Kotlin |
| **UI** | Jetpack Compose |
| **Architecture** | Clean Architecture + MVVM |
| **Local DB** | Room Database |
| **DI** | Dagger Hilt |
| **Notifications** | AlarmManager + NotificationManager |

---
## 🧠 Architecture Overview

```
com.example.grindlog
├── data
│   ├── local
│   │   ├── converter
│   │   ├── dao
│   │   ├── database
│   │   └── entity
│   ├── notification
│   └── repository
├── di
├── domain
│   ├── model
│   ├── notification
│   └── usecase
├── presentation
│   ├── analysis
│   ├── components
│   ├── navigation
│   ├── profile
│   ├── reminder
│   ├── splash
│   ├── today
│   └── todo
├── ui.theme
├── BaseApplication.kt
└── MainActivity.kt
```


