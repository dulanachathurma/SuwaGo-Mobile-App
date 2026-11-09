<div align="center">
  
# SuwaGo Mobile
  
<img src="https://github.com/user-attachments/assets/4809f664-f14e-415a-b76e-3a15eb7035d9" width="90%" alt="Analysis output 1">

### Your smart health companion in your pocket

**Native Android app built with Kotlin and Jetpack Compose**
Track your vitals. Find the right doctor. Ask the AI. Never miss a pill.

<br/>

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%20Design%203-0E8C7F?style=for-the-badge&logo=materialdesign&logoColor=white)
![Version](https://img.shields.io/badge/Version-1.1.0-FDDA04?style=for-the-badge&labelColor=0B2E8A)

<br/>

<img width="1672" height="941" alt="home_banner_2" src="https://github.com/user-attachments/assets/0ab47d04-4a11-43de-ba29-5684e9651eb0" />


</div>

---

## Table of Contents

- [About](#about)
- [Screenshots](#screenshots)
- [Features](#features)
- [What's New in v1.1](#whats-new-in-v11)
- [Run on Your Phone](#run-on-your-phone)
- [Backend](#backend)
- [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [Languages](#languages)
- [Developer](#developer)
- [Disclaimer](#disclaimer)

---

## About

**SuwaGo Mobile** is the native Android version of the VitaCare web app. It brings everyday healthcare into one simple place. You can track six key health numbers and calculate your BMI. You can find the right doctor by illness or by symptoms. You can see clinics and hospitals near you on a free map. You can also chat with an AI assistant and set reminders for your medicine.

The whole app works in **English**, **Sinhala** and **Tamil**. A one tap shortcut on the home screen calls the **1990** emergency ambulance service.

> The app talks to the deployed backend at **https://suwago.vercel.app/**.
> If the URL changes, update `BASE_URL` in
> `app/src/main/java/com/qualityapplied/vitacare/data/ApiClient.kt`.

---

## Screenshots

<div align="center">
  <div style="overflow-x: auto; white-space: nowrap; padding: 10px 0;">
    <img width="260" alt="1000165856" src="https://github.com/user-attachments/assets/ea8db662-d62a-4222-91a2-928d41ea45bd" style="margin-right: 12px; display: inline-block;" />
    <img width="260" alt="1000165864" src="https://github.com/user-attachments/assets/b0c9184b-cce3-456e-84c1-beb5175fd67f" style="margin-right: 12px; display: inline-block;" />
    <img width="260" alt="1000165865-1" src="https://github.com/user-attachments/assets/08c01d8c-2c24-49f6-8d6b-788e64ab6bf0" style="margin-right: 12px; display: inline-block;" />
    <img width="260" alt="1000165863" src="https://github.com/user-attachments/assets/efe4ef5a-72d9-4f44-85d6-6e013434c161" style="margin-right: 12px; display: inline-block;" />
    <img width="260" alt="1000165868" src="https://github.com/user-attachments/assets/81cbd449-0ef7-4b10-af3e-b08070bea26f" style="margin-right: 12px; display: inline-block;" />
    <img width="260" alt="1000165866" src="https://github.com/user-attachments/assets/aa1a0f07-0930-43da-bb11-1b1dbf715cbb" style="margin-right: 12px; display: inline-block;" />
    <img width="260" alt="1000165906" src="https://github.com/user-attachments/assets/b18d0b00-1c1b-42ba-bbd8-6c3abff37e31" style="margin-right: 12px; display: inline-block;" />
    <img width="260" alt="1000165899" src="https://github.com/user-attachments/assets/18c6e051-dbef-4aac-b717-2efb407cbb97" style="margin-right: 12px; display: inline-block;" />
    <img width="260" alt="1000165901" src="https://github.com/user-attachments/assets/7ed8a4db-af1a-4c1d-90dc-aa34a98144f6" style="margin-right: 12px; display: inline-block;" />
    <img width="260" alt="1000165899" src="https://github.com/user-attachments/assets/9a28d93b-12c9-46d9-b357-8c57e8849bd2" style="margin-right: 12px; display: inline-block;" />
    <img width="260" alt="1000165868" src="https://github.com/user-attachments/assets/81cbd449-0ef7-4b10-af3e-b08070bea26f" style="margin-right: 12px; display: inline-block;" />
    <img width="260" alt="1000165869" src="https://github.com/user-attachments/assets/2daa53e5-7cbe-4740-86be-35274024666d" style="display: inline-block;" />
  </div>
</div>
---

## Features

### Account and Session
- Register and login with **JWT**.
- **The session is saved on the phone.** After you close the app it opens straight on the Dashboard. Only the **Logout** button ends the session.
- Change your password from **Profile > Security**.

### Dashboard
- BMI card with a visual scale.
- BMI trend chart.
- Add and delete entries.
- Quick shortcut cards for vitals and reminders.

### Health Tracker
Tracks six core vitals with a status and a trend chart for each one.

| Vital | Unit | Normal range |
|---|---|---|
| Blood Sugar | mg/dL | 70 – 99 |
| Cholesterol | mg/dL | below 200 |
| Blood Pressure | mmHg | 90/60 – 119/79 |
| Heart Rate | bpm | 60 – 100 |
| Blood Oxygen (SpO2) | % | 95 or above |
| Body Temperature | °C | 35.5 – 37.5 |

- Instant status: **Normal**, **High**, **Warning** or **Dangerous**.
- Urgent warnings for dangerous readings.
- Suggests the right **specialist** when a reading is not normal.

### Find a Doctor
- **45 conditions** and **15 specialties** with search.
- **Symptom checker:** tick your symptoms and see the best specialists with a **match percentage**.
- **In-app nearby map** with OpenStreetMap and no API key. Clinics, hospitals and doctors are pinned around you. The list is sorted by distance and every place has a **Directions** button.
- Doctor reviews with star ratings and comments.
- Book a channeling with a date and time slot.

### AI Assistant
- Chat powered by the backend (**Claude**).
- Advice is tuned to your age and height and weight.
- Replies in the language you choose.
- Detects red flag symptoms and tells you to seek emergency care.
- Falls back to a basic **offline answer** if the server route is missing.

### Reminders and Notifications
- Pill reminders with medicine name and dose and time.
- Daily health reminder at the time you choose.
- Bell icon with an **unread badge**.
- Health alert when a saved reading is high or dangerous.

### Lab Report Vault
- Store blood tests and X-rays and prescriptions.
- Add by camera or gallery and filter by category.

### Profile
- Edit your details.
- Choose or remove a profile photo (cropped to 256px like the web app).
- Change password and notification settings.
- Logout button.

---

## What's New in v1.1

| | Feature | Details |
|---|---|---|
| 🩺 | **Symptom checker** | Doctors tab > *By symptoms* > tick symptoms > best specialists with match % |
| 🗺️ | **In-app map** | OpenStreetMap with no API key. Nearby clinics and hospitals and doctors. Directions button |
| 🔑 | **Change password** | Profile > Security. Needs the backend route in `BACKEND_ADDITIONS.md` |
| 🔔 | **Notifications** | Bell with unread badge. Daily health reminder. Alerts for high or dangerous readings |
| 🤖 | **AI assistant tab** | Chat powered by the backend (Claude). Offline fallback if the route is missing |
| 🚪 | **Logout moved** | Logout is now on the Profile screen. The bottom bar has the new AI tab |
| 👤 | **Developer card** | Keeps your photo. Logo and 🩺 icons are unchanged |

---

## Run on Your Phone

1. Open **Android Studio** (Koala or newer) and choose **File > Open**. Select the `VitaCareMobile` folder.
2. Wait for the Gradle sync. It downloads Gradle 8.7 and the libraries. You need internet the first time.
3. On your phone go to **Settings > About phone** and tap **Build number** 7 times. Then open **Developer options** and enable **USB debugging**.
4. Plug in the phone and accept the prompt. Choose it at the top of Android Studio and press **Run**.

---

## Backend

| Endpoint | Method | Used for |
|---|---|---|
| `/api/users/password` | `PUT` | Change password |
| `/api/ai/chat` | `POST` | AI assistant (Claude) |

The base URL is `https://suwago.vercel.app/api`. See `BACKEND_ADDITIONS.md` for the extra routes the app needs.

---

## Project Structure

```text
app/src/main/java/com/qualityapplied/vitacare/
├── MainActivity.kt
├── data/
│   ├── ApiClient            OkHttp client
│   ├── SessionStore         saved login
│   ├── Models
│   ├── DoctorData
│   ├── SymptomData
│   ├── PlacesApi            Overpass (OpenStreetMap)
│   ├── LocationHelper
│   ├── Notifications        + reminder receivers
│   └── AiLocal              offline AI fallback
└── ui/
    ├── AppRoot              header, tabs, auth flow
    ├── AppViewModel
    ├── Util                 tr() and image helpers
    ├── components/
    │   └── Common.kt        buttons, inputs, avatar, language switcher, Canvas line chart
    └── screens/
        ├── Login and Register
        ├── Dashboard
        ├── Metrics
        ├── Doctor
        ├── SymptomChecker
        ├── NearbyMapScreen
        ├── AiAssistantScreen
        ├── NotificationsScreen
        ├── Profile
        └── AboutDeveloper

app/src/main/res/values{,-si,-ta}/strings.xml    209 strings per language
```

---

## Tech Stack

| Area | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose and Material Design 3 |
| State | ViewModel and StateFlow and Coroutines |
| Networking | OkHttp3 and JSON |
| Custom graphics | Android Canvas API (line charts and BMI gauge) |
| Maps | OpenStreetMap with Leaflet in a WebView and the Overpass API |
| Backend | Node.js and Express on Vercel |
| AI | Claude through the backend |

---

## Languages

Switch from the top bar. Your choice is remembered.

**English** · **සිංහල** · **தமிழ்**

The strings are converted from the web i18n files. There are 209 strings per language.

---

## Developer

<div align="center">

<img width="300" height="450" alt="PHOTO-2026-11-09-16-45-01" src="https://github.com/user-attachments/assets/259a87fd-3661-4442-9d0a-6fa04c998f4b" />


### Dulana Chathurma

**AI Integrated Full Stack and UI/UX Developer**
BSc (Hons) Software Engineering Undergraduate · University of Kelaniya

[![Portfolio](https://img.shields.io/badge/Portfolio-0E8C7F?style=for-the-badge&logo=vercel&logoColor=white)](https://dulanaportfolio.vercel.app)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/dulana-chathurma/)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/dulanachathurma)

<br/>
<img width="100%" alt="3f32f871-e354-4f06-89c1-bdf37ed7c36b" src="https://github.com/user-attachments/assets/f90b4ae3-bca2-4c0b-b8a3-c815ed4ec8d0" />

</div>

---

## Disclaimer

The health guidance in this app is **educational and not medical advice**. It does not replace a real doctor. In an emergency call **1990** right away.
