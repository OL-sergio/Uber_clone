# Uber Clone

An Android application that replicates the core functionality of Uber, providing a platform for ride-sharing services with separate interfaces for passengers and drivers.

## Features

### For Passengers
- 🚗 Request rides with real-time location tracking
- 📍 Set pickup and destination locations using Google Maps
- 💰 View trip costs and payment information
- 🔍 Track driver location and estimated arrival time
- 📱 Cancel ride requests when needed

### For Drivers
- 🚙 Accept or decline ride requests
- 🗺️ Navigate to passenger pickup locations
- 📊 View passenger information and trip details
- 💳 Complete trips and process payments
- 📍 Real-time location sharing with passengers

### General Features
- 🔐 User authentication (Login/Register)
- 👤 Separate user roles (Passenger/Driver)
- 🌍 Real-time location tracking with GeoFire
- 🗣️ Multi-language support (Portuguese)
- 🔄 Live trip status updates
- 📱 Modern Android UI with Material Design

## Technology Stack

- **Platform**: Android (API 26+)
- **Language**: Java 8
- **Build System**: Gradle with Kotlin DSL
- **Database**: Firebase Realtime Database & Firestore
- **Authentication**: Firebase Authentication
- **Maps**: Google Maps Android API
- **Location Services**: GeoFire for location-based queries
- **UI**: Android View Binding, Material Design Components

## Prerequisites

Before running this application, make sure you have:

1. **Android Studio** (latest version recommended)
2. **Java 8** or higher
3. **Google Maps API Key**
4. **Firebase Project** with the following services enabled:
   - Authentication
   - Realtime Database
   - Firestore
5. **Android SDK** with minimum API level 26

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/OL-sergio/Uber_clone.git
cd Uber_clone
```

### 2. Firebase Configuration
1. Create a new project in [Firebase Console](https://console.firebase.google.com/)
2. Enable Authentication, Realtime Database, and Firestore
3. Download the `google-services.json` file
4. Place it in the `app/` directory (replace the existing one)

### 3. Google Maps API Setup
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Enable Maps SDK for Android
3. Create an API key
4. Update the API key in `AndroidManifest.xml`:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY_HERE" />
```

### 4. Build and Run
1. Open the project in Android Studio
2. Sync the project with Gradle files
3. Connect an Android device or start an emulator
4. Run the application

## Project Structure

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

## Usage

### Getting Started
1. **Launch the app** and choose your role (Passenger or Driver)
2. **Create an account** or login with existing credentials
3. **Grant location permissions** when prompted

### As a Passenger
1. Set your pickup location (current location by default)
2. Enter your destination
3. Request a ride by tapping "Chamar Uber"
4. Wait for a driver to accept your request
5. Track your driver's location in real-time
6. Complete the trip and view payment details

### As a Driver
1. Wait for ride requests in the "RequestsActivity"
2. View incoming requests with passenger details
3. Accept a ride request
4. Navigate to the passenger's location
5. Pick up the passenger and navigate to destination
6. Complete the trip and process payment

## Key Dependencies

```kotlin
// Firebase
implementation 'com.google.firebase:firebase-auth'
implementation 'com.google.firebase:firebase-database'
implementation 'com.google.firebase:firebase-firestore'

// Google Maps
implementation 'com.google.android.gms:play-services-maps'

// GeoFire for location services
implementation 'com.firebase:geofire-android'

// Android UI
implementation 'androidx.appcompat:appcompat'
implementation 'com.google.android.material:material'
implementation 'androidx.constraintlayout:constraintlayout'
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Security Note

⚠️ **Important**: The Google Maps API key in this repository should be replaced with your own. Never commit API keys to public repositories in production applications.

## License

This project is created for educational purposes. Please ensure you comply with all relevant terms of service for the APIs and services used.

## Support

For questions and support, please open an issue in the GitHub repository.

---

**Note**: This application is a clone created for educational purposes and is not affiliated with Uber Technologies Inc.