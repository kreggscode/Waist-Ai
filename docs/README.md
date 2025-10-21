# 🎯 WaistHip - AI-Powered Health & Nutrition Tracking

## 📱 About

WaistHip is a beautiful, modern Android application for tracking waist-to-hip ratio (WHR) and nutrition with AI-powered food recognition. Built with Jetpack Compose and Material Design 3, it offers a premium $1M+ app experience.

## ✨ Key Features

### 📏 WHR Tracking
- **Precise Measurements**: Track waist and hip measurements
- **Instant Calculations**: Automatic WHR calculation
- **Health Insights**: Get personalized health recommendations
- **Animated Charts**: Beautiful trend visualization with smooth animations

### 📸 AI Food Scanner
- **Smart Recognition**: AI-powered food identification from photos
- **Nutritional Analysis**: Automatic calorie and macro calculations
- **Meal Type Selection**: Choose from Breakfast, Lunch, Dinner, Snack, or Other
- **Quick Logging**: Save meals with a single tap

### 📊 Progress Tracking
- **Visual Charts**: Animated line charts showing WHR trends over time
- **History View**: Complete measurement and meal history
- **Stats Dashboard**: Real-time statistics on home screen
- **Export Data**: Export your data for personal use

### 🎨 Premium Design
- **Glassmorphic UI**: Frosted glass effects with gradient borders
- **Smooth Animations**: Spring physics and fade transitions
- **Dark Mode**: Seamless theme switching
- **Edge-to-Edge**: Modern full-screen design

## 🛠️ Technical Stack

### Frontend
- **Jetpack Compose**: Modern declarative UI
- **Material Design 3**: Latest design system
- **Navigation Component**: Type-safe navigation
- **Coroutines & Flow**: Reactive programming

### Backend
- **Room Database**: Local data persistence
- **Hilt**: Dependency injection
- **KSP**: Kotlin Symbol Processing (not KAPT)
- **ViewModel**: MVVM architecture

### AI & Services
- **Pollinations AI**: Food recognition API
- **CameraX**: Modern camera implementation
- **Canvas API**: Custom chart rendering

## 📂 Project Structure

```
app/src/main/java/com/kreggscode/waisttohip/
├── data/
│   ├── local/          # Room database entities and DAOs
│   ├── repository/     # Data repositories
│   └── ThemeManager.kt # Theme state management
├── ui/
│   ├── components/     # Reusable UI components
│   ├── screens/        # App screens
│   ├── theme/          # Theme colors and typography
│   └── viewmodels/     # ViewModels for screens
└── MainActivity.kt     # Main entry point

docs/
├── index.html                    # Landing page
├── privacy-policy.html           # Privacy policy
├── terms-and-conditions.html     # Terms & conditions
├── app-icon.html                 # Icon generator
└── README.md                     # This file
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17 or later
- Android SDK 34
- Gradle 8.2+

### Build Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/waisthip.git
   cd waisthip
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the project folder

3. **Sync Gradle**
   - Wait for Gradle sync to complete
   - Resolve any dependency issues

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click Run (Shift + F10)

### Important Notes

⚠️ **First Install**: Uninstall any previous version before installing a new build due to database schema changes.

⚠️ **KSP vs KAPT**: This project uses KSP (Kotlin Symbol Processing) instead of deprecated KAPT for better performance and Java 17+ compatibility.

## 📱 Screens

### Home Screen
- Real-time stats from latest measurements
- Recent meals with nutritional info
- Quick action buttons
- Theme toggle

### Measure Screen
- Manual measurement input
- Beautiful circular WHR display
- Instant health status
- Save to history

### Scanner Screen
- Camera preview with AI scanning
- Real-time food recognition
- Meal type selector
- Nutritional breakdown

### History Screen
- **NEW**: Animated line chart showing WHR trends
- Stats summary with change indicators
- Complete measurement history
- Delete functionality

### All Meals Screen
- Complete meal history
- Filter by meal type
- Add new meals
- Nutritional summaries

## 🎨 Design System

### Colors
- **Primary**: Indigo (#6366F1)
- **Secondary**: Purple (#8B5CF6)
- **Accent**: Pink (#EC4899)
- **Teal**: (#14B8A6)
- **Success**: Emerald (#10B981)

### Components
- **GlassCard**: Glassmorphic cards with blur
- **AnimatedStatCard**: Stat cards with spring animations
- **WHRLineChart**: Custom canvas-based line chart
- **MealTypeSelector**: Horizontal scrolling chips
- **FloatingBottomNav**: Glass navigation bar

## 🔧 Recent Updates

### ✅ Completed Features
1. **Navigation Fixes**
   - ✅ Manual Add button now navigates to MeasureScreen
   - ✅ "See All" meals button connects to AllMealsScreen
   - ✅ ScanResults screen added to navigation

2. **History Page Enhancements**
   - ✅ Beautiful animated line chart for WHR trends
   - ✅ Smooth bezier curves with gradient fill
   - ✅ Healthy threshold reference line
   - ✅ Animated point rendering

3. **Web Pages Created**
   - ✅ Stunning landing page with animations
   - ✅ Comprehensive privacy policy
   - ✅ Detailed terms and conditions
   - ✅ App icon generator with 4 styles

## 📄 Documentation

- **Landing Page**: [docs/index.html](./index.html)
- **Privacy Policy**: [docs/privacy-policy.html](./privacy-policy.html)
- **Terms & Conditions**: [docs/terms-and-conditions.html](./terms-and-conditions.html)
- **Icon Generator**: [docs/app-icon.html](./app-icon.html)

## 🔐 Privacy & Security

- **Local Storage**: All data stored on device
- **No Cloud Sync**: Your data never leaves your device
- **AI Processing**: Images processed in real-time, not stored
- **GDPR Compliant**: Full data control and deletion

## 📝 License

Copyright © 2025 WaistHip. All rights reserved.

## 🤝 Contributing

This is a private project. For inquiries, contact: support@waisthip.app

## 📧 Contact

- **Email**: support@waisthip.app
- **Privacy**: privacy@waisthip.app
- **Legal**: legal@waisthip.app

## 🎉 Acknowledgments

- **Jetpack Compose** team for the amazing UI toolkit
- **Material Design** for design guidelines
- **Pollinations AI** for food recognition API
- **Android Community** for continuous support

---

**Built with ❤️ using Jetpack Compose**

*Ready for production deployment!*
