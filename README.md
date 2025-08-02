
## 📱  Uber Clone

<div align="center">

### 🚗 Uber Clone - Ride Sharing Platform

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFA000?style=for-the-badge&logo=firebase&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)


*An Android application that replicates the core functionality of Uber, providing a platform for ride-sharing services with separate interfaces for passengers and drivers.*
  
</div>

## Objetivos

- Practice Android development with **Java** and **Kotlin**.
- Integrate **Firebase** services (authentication, database, notifications).
- Implement geolocation and dynamic permissions.
- Structure a modular and scalable project.
  

<div>
    
 ## Funcionalidade por Classe

- **DriverActivity**: Driver interface to accept, track, and complete rides.
- **LoginActivity**: User authentication screen via Firebase.
- **MainActivity**: Profile selection (passenger or driver).
- **PassengerActivity**: Ride request, destination selection, and driver tracking.
- **RegisterActivity**: New user registration and profile definition.
- **RequestsActivity**: List of ride requests for drivers.
- **RequestsAdapter**: Adapter to display requests in RecyclerView.
- **FirebaseConfiguration**: Firebase initialization and configuration.
- **UserFirebase**: Utility methods for user data manipulation in Firebase.
- **Constants**: Stores global project constants.
- **Locations**: Helper functions for location manipulation.
- **MonitoringUsers**: Monitors user locations in real-time.
- **Permissions**: Location permission management.
- **RecyclerItemClickListener**: Generic listener for clicks on RecyclerView items.
- **TripSummaryDialog**: Dialog to display ride summary.
- **UsersMarkers**: Custom marker management on the map.
- **Destination, Request, RequestActive, Users**: Data models for destinations, requests, ride statuses, and users.


</div>


## ✨ Key Features

<div align="center">

| 👥 **For Passengers**                                  | 🚗 **For Drivers**                               | 🔧 **System Features**                            |
| :----------------------------------------------------- | :------------------------------------------------- | :------------------------------------------------- |
| 🚗 Request rides with real-time tracking               | 🚙 Accept/decline ride requests                    | 🔐 Secure authentication system                    |
| 📍 Set pickup & destination via Maps                   | 🗺️ Navigate to pickup locations                     | 👤 Role-based user interface                       |
| 💰 View trip costs & payment info                       | 📊 View passenger & trip details                   | 🌍 Real-time location tracking                     |
| 🔍 Track driver & arrival time                         | 💳 Process payments & complete trips                 | 🗣️ Multi-language support                           |
| 📱 Cancel rides when needed                             | 📍 Share location with passengers                    | 🔄 Live status updates                              |

</div>

<div>
    
## 🛠️ Technology Stack

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=java&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=black)
![Google Maps](https://img.shields.io/badge/Google%20Maps-4285F4?style=flat-square&logo=googlemaps&logoColor=white)
![Material Design](https://img.shields.io/badge/Material%20Design-757575?style=flat-square&logo=materialdesign&logoColor=white)

</div>
</div>



<div align="center">
    

| Component            | Technology                                   | Purpose                                  |
| :------------------- | :------------------------------------------- | :--------------------------------------- |
| **Platform**         | Android (API 26+)                           | Native mobile application                |
| **Language**         | Java 8                                       | Core development language                |
| **Build System**     | Gradle with Kotlin DSL                      | Project automation                       |
| **Database**         | Firebase Realtime Database & Firestore      | Data persistence                         |
| **Authentication**   | Firebase Authentication                     | User management                          |
| **Maps**             | Google Maps Android API                     | Location services                        |
| **Location Services** | GeoFire                                      | Real-time location queries               |
| **UI Framework**     | Material Design Components                  | Modern interface design                  |

</div>



### 🚀 Quick Start Guide

<details>
<summary><b>🔧 Setup Instructions</b></summary>

#### 1. Clone the Repository
```bash
git clone https://github.com/OL-sergio/Uber_clone.git
cd Uber_clone
```

#### 2. Firebase Configuration
1. Create a new project in [Firebase Console](https://console.firebase.google.com/)
2. Enable Authentication, Realtime Database, and Firestore
3. Download the `google-services.json` file
4. Place it in the `app/` directory

#### 3. Google Maps API Setup
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Enable Maps SDK for Android
3. Create an API key
4. Update the API key in `AndroidManifest.xml`:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY_HERE" />
```

#### 4. Build and Run
1. Open the project in Android Studio
2. Sync the project with Gradle files
3. Connect an Android device or start an emulator
4. Run the application

</details>


<details>
<summary><b>📁 Project Structure</b></summary>

```
app/
├── src/
│   ├── main/
│   │   ├── java/udemy/java/uber_clone/
│   │   │   ├── activity/          # All app activities
│   │   │   │   ├── MainActivity.java
│   │   │   │   ├── LoginActivity.java
│   │   │   │   ├── RegisterActivity.java
│   │   │   │   ├── PassengerActivity.java
│   │   │   │   ├── DriverActivity.java
│   │   │   │   └── RequestsActivity.java
│   │   │   ├── config/            # Firebase configuration
│   │   │   ├── helpers/           # Utility classes
│   │   │   └── model/             # Data models
│   │   ├── res/                   # Resources (layouts, strings, etc.)
│   │   └── AndroidManifest.xml
│   └── test/                      # Unit tests
└── build.gradle.kts               # App-level build configuration
```

</details>

<details>
<summary><b>📱 Usage Guide</b></summary>

#### Getting Started
1. **Launch the app** and choose your role (Passenger or Driver)
2. **Create an account** or login with existing credentials
3. **Grant location permissions** when prompted

#### As a Passenger
1. Set your pickup location (current location by default)
2. Enter your destination
3. Request a ride by tapping "Chamar Uber"
4. Wait for a driver to accept your request
5. Track your driver's location in real-time
6. Complete the trip and view payment details

#### As a Driver
1. Wait for ride requests in the "RequestsActivity"
2. View incoming requests with passenger details
3. Accept a ride request
4. Navigate to the passenger's location
5. Pick up the passenger and navigate to destination
6. Complete the trip and process payment

</details>

<details>
<summary><b>📦 Key Dependencies</b></summary>

```kotlin
// Firebase Services
implementation 'com.google.firebase:firebase-auth'
implementation 'com.google.firebase:firebase-database'
implementation 'com.google.firebase:firebase-firestore'

// Google Services
implementation 'com.google.android.gms:play-services-maps'
implementation 'com.google.android.gms:play-services-location'

// Location Services
implementation 'com.firebase:geofire-android'

// Android UI Components
implementation 'androidx.appcompat:appcompat'
implementation 'com.google.android.material:material'
implementation 'androidx.constraintlayout:constraintlayout'
```

</details>
