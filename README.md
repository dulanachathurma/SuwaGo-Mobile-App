# VitaCare Mobile (Android – Kotlin / Jetpack Compose)

Native Android version of the VitaCare web app. Talks to the deployed backend:
**https://suwago.vercel.app/api** (change `BASE_URL` in
`app/src/main/java/com/qualityapplied/vitacare/data/ApiClient.kt` if the URL changes).

## Run on your phone
1. Android Studio (Koala or newer) -> **File > Open** -> select this `VitaCareMobile` folder.
2. Wait for Gradle sync (it downloads Gradle 8.7 + libraries; internet needed the first time).
3. Phone: Settings > About phone > tap *Build number* 7 times > Developer options > enable **USB debugging**.
4. Plug in the phone, accept the prompt, choose it at the top of Android Studio and press **Run**.

## What's new in v1.1
- **Symptom checker** (old DoctorFinder idea): Doctors tab -> *By symptoms* -> tick symptoms -> best specialists with match %
- **In-app map** (OpenStreetMap, no API key): nearby clinics / hospitals / doctors pinned around you, list sorted by distance, *Directions* button
- **Change password** (Profile -> Security) - needs the backend route in `BACKEND_ADDITIONS.md`
- **Notifications**: 🔔 bell with unread badge, daily health reminder (choose the time), health alert when a saved reading is high/dangerous
- **AI assistant** tab 🤖 - chat powered by the backend (Claude); falls back to a basic offline answer if the server route is missing
- Logout button moved to the Profile screen (bottom bar now has the AI tab)
- Developer card keeps your photo; logo + 🩺 icons unchanged

## Features (same as the web app)
- Register / Login (JWT) – **session is saved on the phone**: after closing the app it opens straight on
  the Dashboard. Only the **Logout** button (bottom bar) ends the session.
- Dashboard: BMI card + scale, BMI trend chart, add / delete entries
- Health tracker: blood sugar, cholesterol, blood pressure, heart rate, oxygen, temperature,
  trend chart, status, urgent warnings, specialist suggestion
- Find a doctor: 45 conditions, 15 specialties, search, symptom checker, in-app nearby map
- Profile: edit details, choose / remove profile photo (cropped to 256px, same as web), change password, notifications, logout
- Languages: **English / සිංහල / தமிழ்** – switch from the top bar; the choice is remembered
- VitaCare logo = launcher icon + login + header. Developer card on the Profile screen.

## Structure
```
app/src/main/java/com/qualityapplied/vitacare/
  MainActivity.kt
  data/   ApiClient (OkHttp), SessionStore (saved login), Models, DoctorData, SymptomData,
          PlacesApi (Overpass), LocationHelper, Notifications (+ reminder receivers), AiLocal
  ui/     AppRoot (header, tabs, auth flow), AppViewModel, Util (tr(), image helpers)
  ui/components/Common.kt     buttons, inputs, avatar, language switcher, Canvas line chart
  ui/screens/                 Login/Register, Dashboard, Metrics, Doctor, SymptomChecker, NearbyMapScreen,
                              AiAssistantScreen, NotificationsScreen, Profile, AboutDeveloper
app/src/main/res/values{,-si,-ta}/strings.xml   209 strings per language (converted from the web i18n files)
```
Disclaimer: the health guidance is educational and not medical advice.
