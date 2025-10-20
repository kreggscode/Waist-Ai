Hey Cortana# Curve & Fuel - Premium AI Health Coach App

A stunning, $1M+ quality Android application for waist-to-hip ratio tracking, calorie management, and AI-powered health coaching. Built with Jetpack Compose and modern Android architecture.

## 🎨 Design Philosophy

**Premium Glass-Morphism iPhone-Inspired UI**
- Edge-to-edge layouts with safe area handling
- Soft shadows and neon accents
- Ice-cream themed progress visualizations
- Rounded 2xl corners (24dp) throughout
- Elastic animations with spring physics
- Polished micro-interactions

## 🌈 Color Palette

### Primary Colors
- **Coral**: `#FF7A8A` - Primary actions and highlights
- **Mint**: `#7BE6C3` - Secondary actions and success states
- **Lavender**: `#C9B2FF` - Tertiary elements and AI features
- **Midnight**: `#0B0F1A` - Dark mode backgrounds

### Ice Cream Tones
- **Vanilla**: `#FFF8E7` - Light backgrounds
- **Strawberry**: `#FFE0E6` - Waist progress rings
- **Pistachio**: `#E8F5E8` - Hip progress rings
- **Blueberry**: `#E6E8FF` - WHR progress rings

### Neon Accents
- **Neon Pink**: `#FF00FF`
- **Neon Cyan**: `#00FFFF`
- **Neon Purple**: `#8800FF`
- Used for AI features and gradient overlays

## 📱 Features

### 1. **Stunning Splash Screen**
- Animated ice-cream scoops floating in glass-morphism cards
- Gradient background with parallax effects
- Three-step intro: Measure → Track → Expert AI
- Pulsing dots loader with elastic animations

### 2. **Home Dashboard**
- Personalized greeting based on time of day
- Animated stat cards with gradient borders
- Calorie summary strip with macro breakdown
- Quick actions: Scan Food, Quick Add, Email Log
- Recent meals with emoji indicators
- AI insights card with pulsing animations
- Floating Measure FAB with gradient background

### 3. **Measurement Flow**
- **Method Selection**: Camera AR or Manual Input
- **Camera Mode**: AR-style overlay with measurement guides
- **Manual Input**: Large increment buttons with sliders
- **Results**: Stunning ice-cream scoop progress rings
  - Outer ring: Waist (Strawberry gradient)
  - Middle ring: Hip (Pistachio gradient)
  - Inner ring: WHR (Blueberry gradient)
  - Animated with elastic easing (900ms)
  - Melting droplet micro-animations

### 4. **AI Analysis Screen**
- Animated AI avatar with pulsing effects
- Loading animation with rotating gradient arcs
- Typing animation for AI responses
- Risk assessment badges (Healthy/Moderate/High Risk)
- Personalized recommendations with expand/collapse
- Calorie and protein targets
- Diet plan CTA with gradient buttons

### 5. **Food Scanner**
- **Camera Scanner**: Real-time scanning with animated overlay
- **Email Integration**: Forward meal emails to scan@curvefuel.ai
- **Receipt Scanner**: Upload grocery/restaurant receipts
- **Results Screen**: 
  - Nutrition summary with animated progress bars
  - Editable food items with quantity selectors
  - Macro breakdown (Protein, Carbs, Fat)
  - Premium food item cards with expand/collapse

### 6. **AI Chat**
- Full-screen chat interface
- Quick suggestion chips
- Typing indicators with pulsing dots
- Message bubbles with gradient backgrounds
- Contextual AI responses
- TTS readback capability (planned)

### 7. **Floating Bottom Navigation**
- Glass-morphism pill design
- Elevated with 12dp shadow
- Respects Android system gesture area (16dp safe inset)
- Smooth color transitions (300ms)
- Icons: Home, Measure, Scan, Chat

## 🏗️ Architecture

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 36
- **Compile SDK**: 36

### Dependencies
- **Compose BOM**: 2024.02.00
- **Navigation**: Compose Navigation 2.7.7
- **DI**: Hilt 2.48 with KSP (NOT KAPT)
- **Database**: Room 2.6.1 with KSP
- **Async**: Coroutines + Flow
- **Image Loading**: Coil 2.5.0
- **Animations**: Lottie 6.3.0
- **Camera**: CameraX 1.3.1
- **ML**: ML Kit Text Recognition 16.0.0
- **System UI**: Accompanist 0.32.0

### Project Structure
```
app/
├── src/main/
│   ├── java/com/kreggscode/waisttohip/
│   │   ├── ui/
│   │   │   ├── components/      # Reusable UI components
│   │   │   │   └── GlassComponents.kt
│   │   │   ├── screens/         # Screen composables
│   │   │   │   ├── SplashScreen.kt
│   │   │   │   ├── HomeScreen.kt
│   │   │   │   ├── MeasureScreen.kt
│   │   │   │   ├── AIAnalysisScreen.kt
│   │   │   │   ├── ScannerScreen.kt
│   │   │   │   ├── ScanResultsScreen.kt
│   │   │   │   └── ChatScreen.kt
│   │   │   └── theme/           # Design system
│   │   │       ├── Color.kt
│   │   │       ├── Type.kt
│   │   │       └── Theme.kt
│   │   ├── MainActivity.kt
│   │   └── CurveAndFuelApplication.kt
│   ├── res/
│   │   ├── values/
│   │   │   ├── colors.xml
│   │   │   ├── strings.xml
│   │   │   └── themes.xml
│   │   └── xml/
│   │       ├── file_paths.xml
│   │       ├── backup_rules.xml
│   │       └── data_extraction_rules.xml
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## 🎯 Key Components

### GlassCard
Premium glass-morphism card with blur effects and gradient borders.
```kotlin
GlassCard(
    cornerRadius = 24.dp,
    borderWidth = 1.dp,
    glassColor = GlassWhite,
    borderGradient = listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.1f)),
    shadowElevation = 8.dp
) {
    // Content
}
```

### IceCreamProgressRing
Multi-ring animated progress indicator with ice-cream scoop design.
```kotlin
IceCreamProgressRing(
    waistProgress = 0.8f,
    hipProgress = 0.75f,
    whrValue = 0.84f,
    whrStatus = "Healthy",
    animationDuration = 900
)
```

### AnimatedGradientButton
Spring-animated button with flowing gradient background.
```kotlin
AnimatedGradientButton(
    text = "Continue",
    onClick = { },
    gradientColors = listOf(Coral, Lavender, Mint)
)
```

### FloatingBottomNav
Glass-morphism bottom navigation with safe area handling.
```kotlin
FloatingBottomNav(
    selectedIndex = 0,
    onItemSelected = { index -> }
)
```

## 🎨 Typography

### Font Scales
- **Display Large**: 48sp, Bold - Splash screen hero text
- **Display Medium**: 40sp, Bold - Section headers
- **Display Small**: 32sp, SemiBold - Card titles
- **Headline Large**: 28sp, SemiBold - Screen titles
- **Headline Medium**: 24sp, SemiBold - Subsection headers
- **Headline Small**: 20sp, Medium - Component headers
- **Body Large**: 16sp, Normal - Primary content
- **Body Medium**: 14sp, Normal - Secondary content
- **Label Large**: 14sp, Medium - Buttons and navigation

## 🎬 Animations

### Timing
- **Screen Transitions**: 400ms slide + fade
- **Button Press**: Spring physics (DampingRatioMediumBouncy)
- **Progress Rings**: 900ms elastic easing
- **Micro-interactions**: 300ms ease-in-out
- **AI Pulse**: 1500ms infinite reverse

### Spring Physics
```kotlin
spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)
```

## 🔧 Setup Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 36
- Gradle 8.2+

### Build Steps
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on emulator (API 26+) or physical device

### Important Notes
- **KSP is used instead of KAPT** - Do not change to KAPT
- Edge-to-edge is enabled - System bars are transparent
- Camera permissions required for scanning features
- Internet permission for AI features

## 📐 Design Tokens

### Spacing
- **Micro**: 4dp
- **Small**: 8dp
- **Medium**: 12dp
- **Base**: 16dp
- **Large**: 20dp
- **XLarge**: 24dp
- **XXLarge**: 32dp

### Corner Radius
- **Small**: 8dp
- **Medium**: 12dp
- **Large**: 16dp
- **XLarge**: 20dp
- **2XL**: 24dp (Primary)
- **3XL**: 32dp
- **Full**: CircleShape

### Elevation
- **Low**: 4dp
- **Medium**: 8dp
- **High**: 12dp
- **XHigh**: 16dp
- **Floating**: 24dp

## 🤖 AI Integration

### Polynational AI Client (Placeholder)
```kotlin
// Example request schema
{
    "user_profile": {
        "whr": 0.84,
        "waist": 32,
        "hip": 38,
        "age": 30,
        "gender": "female"
    },
    "recent_logs": [
        { "meal": "Grilled Chicken Salad", "calories": 420 }
    ],
    "query": "Create a weekly meal plan"
}

// Example response schema
{
    "analysis": "Your WHR is in the healthy range...",
    "risk_level": "low",
    "calorie_target": 2000,
    "macros": {
        "protein": 120,
        "carbs": 200,
        "fat": 65
    },
    "recommendations": [
        {
            "title": "Nutrition Optimization",
            "details": "Focus on whole foods...",
            "action_items": ["Increase protein", "Add vegetables"]
        }
    ]
}
```

## 📊 OCR & Food Recognition

### ML Kit Integration
- On-device text recognition
- Barcode scanning support
- Privacy-first approach

### Nutrition API (Planned)
- Food item → nutrition mapping
- Portion size estimation
- Manual editing fallback

## 🔐 Privacy & Permissions

### Required Permissions
- `CAMERA` - For food scanning and measurements
- `INTERNET` - For AI features
- `READ_MEDIA_IMAGES` - For receipt uploads

### Privacy Features
- Email processing: Secure and auto-deleted
- Local data storage with Room
- No data sharing without consent
- Opt-in for AI features

## 🚀 Future Enhancements

- [ ] Weekly meal plan generator
- [ ] Shopping list export
- [ ] Progress charts and trends
- [ ] Social sharing features
- [ ] Wearable integration
- [ ] TTS for AI responses
- [ ] Multi-language support
- [ ] Dark mode optimization
- [ ] Widget support
- [ ] Export to CSV/PDF

## 📄 License

Copyright © 2024 KreggsCode. All rights reserved.

## 👨‍💻 Developer

Built with ❤️ by KreggsCode

---

**Package**: `com.kreggscode.waisttohip`  
**Version**: 1.0.0  
**Build**: Release Candidate
