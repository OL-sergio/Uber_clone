<div align="center">

# 🚀 OL-Sergio's Development Portfolio

[![Portfolio](https://img.shields.io/badge/Portfolio-Developer-blue?style=for-the-badge&logo=github)](https://github.com/OL-sergio)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)

*A comprehensive collection of mobile applications and collaborative systems showcasing modern development practices*

---

</div>

## 📋 Table of Contents

- [🎯 Portfolio Overview](#-portfolio-overview)
- [📱 Featured Application: Uber Clone](#-featured-application-uber-clone)
- [🌟 Application Portfolio](#-application-portfolio)
- [🤝 Collaborative Systems](#-collaborative-systems)
- [🛠️ Technology Stack](#️-technology-stack)
- [🚀 Getting Started](#-getting-started)
- [📞 Contact & Support](#-contact--support)

---

## 🎯 Portfolio Overview

Welcome to my development portfolio! This repository showcases a diverse range of applications and systems, from ride-sharing platforms to collaborative scouting solutions. Each project demonstrates different aspects of modern mobile and web development.

### 🌟 Highlights
- **📱 Mobile Applications**: Native Android apps with modern UI/UX
- **🔥 Firebase Integration**: Real-time databases and authentication
- **🗺️ Location Services**: GPS tracking and mapping functionality
- **🤝 Collaborative Systems**: Integrated multi-service architectures
- **🎨 Visual Design**: Material Design and custom UI components

---

## 📱 Featured Application: Uber Clone

<div align="center">

### 🚗 Uber Clone - Ride Sharing Platform

*An Android application that replicates the core functionality of Uber, providing a platform for ride-sharing services with separate interfaces for passengers and drivers.*

[![Download APK](https://img.shields.io/badge/Download-APK-green?style=for-the-badge&logo=android)](link-to-apk)
[![Live Demo](https://img.shields.io/badge/Live-Demo-blue?style=for-the-badge&logo=youtube)](link-to-demo)

</div>

### ✨ Key Features

<div align="center">

| 👥 **For Passengers** | 🚗 **For Drivers** | 🔧 **System Features** |
|:---------------------|:-------------------|:----------------------|
| 🚗 Request rides with real-time tracking | 🚙 Accept/decline ride requests | 🔐 Secure authentication system |
| 📍 Set pickup & destination via Maps | 🗺️ Navigate to pickup locations | 👤 Role-based user interface |
| 💰 View trip costs & payment info | 📊 View passenger & trip details | 🌍 Real-time location tracking |
| 🔍 Track driver & arrival time | 💳 Process payments & complete trips | 🗣️ Multi-language support |
| 📱 Cancel rides when needed | 📍 Share location with passengers | 🔄 Live status updates |

</div>

### 🛠️ Technology Stack

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=java&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=black)
![Google Maps](https://img.shields.io/badge/Google%20Maps-4285F4?style=flat-square&logo=googlemaps&logoColor=white)
![Material Design](https://img.shields.io/badge/Material%20Design-757575?style=flat-square&logo=materialdesign&logoColor=white)

</div>

| Component | Technology | Purpose |
|-----------|------------|---------|
| **Platform** | Android (API 26+) | Native mobile application |
| **Language** | Java 8 | Core development language |
| **Build System** | Gradle with Kotlin DSL | Project automation |
| **Database** | Firebase Realtime Database & Firestore | Data persistence |
| **Authentication** | Firebase Authentication | User management |
| **Maps** | Google Maps Android API | Location services |
| **Location Services** | GeoFire | Real-time location queries |
| **UI Framework** | Material Design Components | Modern interface design |

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

---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 🌟 Application Portfolio

<div align="center">

*A diverse collection of applications showcasing different domains and technologies*

</div>

### 🍕 iFood Clone
> **Food Delivery Platform**

[![Repository](https://img.shields.io/badge/Repository-iFood-orange?style=flat-square&logo=github)](https://github.com/OL-sergio/iFood)
[![Android](https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com/)

A comprehensive food delivery application inspired by iFood, featuring restaurant listings, menu browsing, order management, and real-time delivery tracking.

**Key Features:**
- 🍽️ Restaurant discovery and browsing
- 📱 Interactive menu with customization options
- 🛒 Shopping cart and order management
- 📍 Real-time delivery tracking
- 💳 Multiple payment methods integration

---

### 🌤️ YupWeather
> **Weather Forecast Application**

[![Repository](https://img.shields.io/badge/Repository-YupWeather-blue?style=flat-square&logo=github)](https://github.com/OL-sergio/YupWeather)
[![Weather API](https://img.shields.io/badge/Weather%20API-OpenWeather-blue?style=flat-square&logo=openweathermap)](https://openweathermap.org/)

A beautiful weather application providing accurate forecasts with an intuitive interface and location-based weather updates.

**Key Features:**
- 🌡️ Current weather conditions and forecasts
- 📍 Location-based weather detection
- 📊 Weather charts and historical data
- 🌙 Day/night mode interface
- ⚡ Weather alerts and notifications

---

### 💬 Instagra-chat
> **Social Media Chat Platform**

[![Repository](https://img.shields.io/badge/Repository-Instagra--chat-purple?style=flat-square&logo=github)](https://github.com/OL-sergio/Instagra-chat)
[![Real-time](https://img.shields.io/badge/Real--time-Socket.io-black?style=flat-square&logo=socketdotio)](https://socket.io/)

An Instagram-inspired social chat application with photo sharing, real-time messaging, and social networking features.

**Key Features:**
- 📸 Photo sharing with filters and effects
- 💬 Real-time messaging and chat rooms
- 👥 User profiles and social connections
- ❤️ Like, comment, and sharing system
- 📱 Stories and status updates

---

### 🐦 Flappybird
> **Classic Game Implementation**

[![Repository](https://img.shields.io/badge/Repository-Flappybird-yellow?style=flat-square&logo=github)](https://github.com/OL-sergio/Flappybird)
[![Game](https://img.shields.io/badge/Game-Android-green?style=flat-square&logo=gamepad)](https://developer.android.com/games)

A classic Flappy Bird game implementation with smooth animations, scoring system, and engaging gameplay mechanics.

**Key Features:**
- 🎮 Intuitive tap-to-fly controls
- 🏆 High score tracking and leaderboards
- 🎵 Sound effects and background music
- 🌈 Colorful graphics and animations
- 📊 Game statistics and achievements

---

### ✅ Tasks
> **Task Management Application**

[![Repository](https://img.shields.io/badge/Repository-Tasks-green?style=flat-square&logo=github)](https://github.com/OL-sergio/Tasks)
[![Productivity](https://img.shields.io/badge/Productivity-Task%20Manager-green?style=flat-square&logo=todoist)](https://todoist.com/)

A comprehensive task management application for organizing daily activities, setting reminders, and tracking productivity.

**Key Features:**
- ✏️ Create, edit, and organize tasks
- 📅 Calendar integration and scheduling
- 🔔 Smart notifications and reminders
- 📊 Progress tracking and analytics
- 🏷️ Categories and priority management

---

### 🏥 MedUtent
> **Medical Utility Application**

[![Repository](https://img.shields.io/badge/Repository-MedUtent-red?style=flat-square&logo=github)](https://github.com/OL-sergio/MedUtent)
[![Healthcare](https://img.shields.io/badge/Healthcare-Medical-red?style=flat-square&logo=heart)](https://www.who.int/)

A medical utility application providing health tracking, medication reminders, and medical information management.

**Key Features:**
- 💊 Medication tracking and reminders
- 📋 Health records and history
- 🏥 Doctor appointments scheduling
- 📊 Health metrics and charts
- 🚨 Emergency contact integration

---

### 🎨 MiniPaint
> **Digital Painting Application**

[![Repository](https://img.shields.io/badge/Repository-MiniPaint-pink?style=flat-square&logo=github)](https://github.com/OL-sergio/MiniPaint)
[![Creative](https://img.shields.io/badge/Creative-Digital%20Art-pink?style=flat-square&logo=adobe)](https://www.adobe.com/)

A lightweight digital painting and drawing application with various brushes, colors, and creative tools.

**Key Features:**
- 🖌️ Multiple brush types and sizes
- 🎨 Color palette and custom colors
- 📐 Drawing tools and shapes
- 💾 Save and export artwork
- ↩️ Undo/redo functionality

---

### 🛒 OLX Clone
> **Marketplace Platform**

[![Repository](https://img.shields.io/badge/Repository-OLX-blue?style=flat-square&logo=github)](https://github.com/OL-sergio/OLX)
[![E-commerce](https://img.shields.io/badge/E--commerce-Marketplace-blue?style=flat-square&logo=shopify)](https://www.shopify.com/)

A marketplace application inspired by OLX, enabling users to buy and sell products with advanced search and filtering capabilities.

**Key Features:**
- 🏪 Product listings and browsing
- 🔍 Advanced search and filters
- 💬 In-app messaging system
- 📸 Photo upload and gallery
- 💰 Price negotiation features

---

## 🤝 Collaborative Systems

<div align="center">

*Integrated multi-service architecture for comprehensive scouting solutions*

</div>

### 🔍 Scouting Ecosystem

A comprehensive scouting platform consisting of three interconnected applications working together to provide a complete solution for talent identification and management.

<div align="center">

```mermaid
graph TD
    A[Scounting Mobile App] --> B[ScountingApi]
    B --> C[ScountingDataBaseSql]
    C --> B
    B --> A
    
    A --> D[User Interface]
    B --> E[Business Logic]
    C --> F[Data Storage]
    
    style A fill:#e1f5fe
    style B fill:#f3e5f5
    style C fill:#e8f5e8
```

</div>

#### 📱 Scounting
> **Main Scouting Application**

[![Repository](https://img.shields.io/badge/Repository-Scounting-indigo?style=flat-square&logo=github)](https://github.com/OL-sergio/Scounting)
[![Mobile](https://img.shields.io/badge/Mobile-Android-green?style=flat-square&logo=android)](https://developer.android.com/)

The primary mobile application providing intuitive interfaces for scouts, coaches, and administrators to manage talent identification processes.

**Key Features:**
- 👥 Scout and player profile management
- 📊 Performance analytics and reports
- 📍 Location-based scouting events
- 📱 Real-time data synchronization
- 🎯 Advanced search and filtering

---

#### 🌐 ScountingApi
> **REST API Service**

[![Repository](https://img.shields.io/badge/Repository-ScountingApi-purple?style=flat-square&logo=github)](https://github.com/OL-sergio/ScountingApi)
[![API](https://img.shields.io/badge/API-REST-purple?style=flat-square&logo=fastapi)](https://fastapi.tiangolo.com/)

A robust REST API service handling all business logic, authentication, and data processing for the scouting ecosystem.

**Key Features:**
- 🔐 Secure authentication and authorization
- 📡 RESTful API endpoints
- 🔄 Real-time data processing
- 📊 Analytics and reporting engine
- 🛡️ Data validation and security

---

#### 🗄️ ScountingDataBaseSql
> **Database Layer**

[![Repository](https://img.shields.io/badge/Repository-ScountingDataBaseSql-teal?style=flat-square&logo=github)](https://github.com/OL-sergio/ScountingDataBaseSql)
[![Database](https://img.shields.io/badge/Database-SQL-teal?style=flat-square&logo=postgresql)](https://www.postgresql.org/)

Comprehensive database design and management system providing optimized data storage and retrieval for the scouting platform.

**Key Features:**
- 🗃️ Optimized database schema design
- 📈 Performance tuning and indexing
- 🔄 Data migration and versioning
- 🛡️ Backup and recovery procedures
- 📊 Database analytics and monitoring

### 🔗 System Integration

The three applications work seamlessly together:

1. **Scounting** provides the user interface and mobile experience
2. **ScountingApi** handles all business logic and API communication
3. **ScountingDataBaseSql** manages data persistence and retrieval

This architecture ensures scalability, maintainability, and optimal performance across the entire scouting ecosystem.

---

## 🛠️ Technology Stack

<div align="center">

*Technologies and frameworks used across the portfolio*

</div>

### 📱 Mobile Development
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)

### 🔥 Backend & Cloud
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Node.js](https://img.shields.io/badge/Node.js-43853D?style=for-the-badge&logo=node.js&logoColor=white)
![REST API](https://img.shields.io/badge/REST-API-blue?style=for-the-badge&logo=fastapi&logoColor=white)

### 🗄️ Database
![SQL](https://img.shields.io/badge/SQL-Database-teal?style=for-the-badge&logo=postgresql&logoColor=white)
![Firestore](https://img.shields.io/badge/Firestore-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Realtime DB](https://img.shields.io/badge/Realtime-Database-orange?style=for-the-badge&logo=firebase&logoColor=white)

### 🎨 UI/UX
![Material Design](https://img.shields.io/badge/Material%20Design-757575?style=for-the-badge&logo=materialdesign&logoColor=white)
![Custom UI](https://img.shields.io/badge/Custom-UI-purple?style=for-the-badge&logo=figma&logoColor=white)

### 🌐 APIs & Services
![Google Maps](https://img.shields.io/badge/Google%20Maps-4285F4?style=for-the-badge&logo=googlemaps&logoColor=white)
![Weather API](https://img.shields.io/badge/Weather-API-blue?style=for-the-badge&logo=openweathermap&logoColor=white)
![Real-time](https://img.shields.io/badge/Real--time-Socket.io-black?style=for-the-badge&logo=socketdotio&logoColor=white)

---

## 🚀 Getting Started

### 📋 Prerequisites

Before running any application, ensure you have:

- **Android Studio** (latest version)
- **Java 8+** or **Kotlin** support
- **Google Maps API Key** (for location-based apps)
- **Firebase Project** with required services
- **Android SDK** (minimum API level varies by project)

### 🔧 Quick Setup

1. **Clone any repository:**
   ```bash
   git clone https://github.com/OL-sergio/[repository-name].git
   cd [repository-name]
   ```

2. **Configure Firebase:**
   - Create Firebase project
   - Download `google-services.json`
   - Place in `app/` directory

3. **Setup API Keys:**
   - Configure required API keys in `AndroidManifest.xml`
   - Update configuration files as needed

4. **Build and Run:**
   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

### 📚 Detailed Setup

Each repository contains specific setup instructions in its individual README file. Please refer to the respective documentation for detailed configuration steps.

---

## 📞 Contact & Support

<div align="center">

### 👨‍💻 OL-Sergio

[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/OL-sergio)
[![Portfolio](https://img.shields.io/badge/Portfolio-FF5722?style=for-the-badge&logo=todoist&logoColor=white)](https://github.com/OL-sergio)
[![Email](https://img.shields.io/badge/Email-D14836?style=for-the-badge&logo=gmail&logoColor=white)](mailto:contact@example.com)

*Passionate mobile developer creating innovative solutions*

</div>

### 🤝 Contributing

Contributions are welcome across all projects! Please feel free to:

- 🐛 Report bugs and issues
- 💡 Suggest new features
- 🔀 Submit pull requests
- ⭐ Star repositories you find useful

### 📄 License

Individual projects may have different licenses. Please check each repository for specific license information.

### 🔒 Security Note

⚠️ **Important**: All API keys shown in public repositories are examples only. Replace with your own keys for production use.

---

<div align="center">

### 🌟 Thanks for visiting!

*This portfolio showcases various projects developed for educational and demonstration purposes. Each application represents different aspects of modern mobile and web development.*

[![Back to Top](https://img.shields.io/badge/Back%20to%20Top-000000?style=for-the-badge&logo=github&logoColor=white)](#-ol-sergios-development-portfolio)

</div>