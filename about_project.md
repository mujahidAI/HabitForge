# 📱 HabitForge - Project Overview

**HabitForge** is a modern Android habit tracking application built with **Kotlin** and **Jetpack Compose**. This document provides a comprehensive analysis of the project structure, architecture, and features.

---

## 🏗️ Architecture & Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite) for local persistence
- **Networking**: Retrofit for API calls
- **Navigation**: Jetpack Navigation Compose

---

## 📂 Project Structure

```
app/src/main/
├── AndroidManifest.xml
├── java/com/example/habitforge/
│   ├── MainActivity.kt                    # Entry point
│   ├── data/                              # Data layer
│   │   ├── Habit.kt                       # Entity model
│   │   ├── HabitDao.kt                    # Database operations
│   │   ├── HabitDatabase.kt               # Room database
│   │   ├── QuoteApiClient.kt              # Retrofit client
│   │   └── QuoteApiService.kt             # API interface
│   ├── navigation/
│   │   └── AppNavGraph.kt                 # Navigation setup
│   ├── ui/                                # UI screens
│   │   ├── HomeScreen.kt                  # Main habit list
│   │   ├── AddHabitScreen.kt              # Add new habit
│   │   ├── HabitDetailsScreen.kt          # Edit/delete habit
│   │   └── theme/                         # Material 3 theming
│   │       ├── Color.kt
│   │       ├── Theme.kt
│   │       └── Type.kt
│   └── viewmodel/                         # Business logic
│       ├── HabitViewModel.kt              # Main ViewModel
│       ├── HabitViewModelFactory.kt       # Factory pattern
│       └── HabitViewModelFactoryProvider.kt
└── res/                                   # Resources (icons, strings, etc.)
```

---

## ✨ Key Features

### 1. **Habit Management**
- ✅ Create, read, update, and delete habits
- ✅ Mark habits as completed
- ✅ Track habit streaks (consecutive days)
- ✅ Persist data locally with Room database

### 2. **Streak Tracking**
- Automatically calculates streaks based on completion dates
- Resets streak if a day is missed
- Stores `lastCompletedDate` to determine continuity
- Visual display of current streak on each habit card

### 3. **Daily Motivational Quotes**
- Fetches quotes from **ZenQuotes API** (`https://zenquotes.io/api/random`)
- Caches quote for 24 hours using SharedPreferences
- Displays on home screen for daily inspiration
- Graceful fallback to default quotes on network errors

### 4. **Navigation**
Three main screens with seamless navigation:
- **Home** (`/home`) - List of all habits with streaks and completion status
- **Add Habit** (`/add`) - Create new habit with title and description
- **Habit Details** (`/details/{habitId}`) - Edit or delete existing habit

---

## 🔍 Code Highlights

### **Data Model** (`Habit.kt`)
```kotlin
@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val streak: Int = 0,                    // 🔥 Streak tracking
    val lastCompletedDate: String? = null   // 🔥 Date tracking
)
```

### **Streak Logic** (`HabitViewModel.kt`)
The streak calculation logic:
- Increments streak if completed on consecutive days (yesterday → today)
- Maintains streak if completed same day multiple times
- Resets to 1 if there's a gap in completion
- Uses `LocalDate` for accurate date comparison

### **API Integration**
- Uses Retrofit with Gson converter for JSON parsing
- Singleton pattern for API client (`QuoteApiClient`)
- Suspend functions for coroutine-based async operations
- Error handling with try-catch and fallback quotes

### **Database**
- Room Database version 2
- Destructive migration enabled for schema changes
- Singleton pattern for database instance
- DAO pattern for clean data access

---

## 🎨 UI/UX Design

- **Material 3 Design System** with modern components
- **Dynamic Color Support** (Android 12+) - adapts to system wallpaper
- **Dark/Light Theme** support based on system preferences
- **Floating Action Button** for quick habit creation
- **Card-based Layout** for habit items with visual hierarchy
- **Color-coded Icons** for completion status (primary = completed, error = incomplete)
- **Responsive Layout** with proper spacing and padding

---

## 🔧 Technical Details

### **Requirements**
- **Minimum SDK**: API 26+ (Android 8.0 Oreo)
  - Required for `LocalDate` API usage
- **Target SDK**: Latest Android version
- **Permissions**: `INTERNET` for quote API access

### **Architecture Patterns**
1. **MVVM**: Clear separation between UI and business logic
2. **Repository Pattern**: DAO acts as data source abstraction
3. **Factory Pattern**: ViewModel creation with dependencies
4. **Singleton Pattern**: Database and API client instances
5. **Observer Pattern**: StateFlow for reactive UI updates

### **Coroutines & Flow**
- `viewModelScope` for lifecycle-aware coroutines
- `MutableStateFlow` for state management
- `collectAsState()` for Compose integration
- Proper suspend function usage for database and network calls

### **Database Schema**
- **Table**: `habits`
- **Version**: 2
- **Migration Strategy**: Destructive (data loss on schema change)

---

## 📱 Screen Breakdown

### **Home Screen** (`HomeScreen.kt`)
- Displays daily motivational quote at the top
- LazyColumn list of all habits
- Each habit card shows:
  - Title and current streak
  - Completion checkbox with color indicator
  - Clickable to navigate to details
- FAB button to add new habits

### **Add Habit Screen** (`AddHabitScreen.kt`)
- Simple form with two fields:
  - Title (required)
  - Description (optional)
- Save button validates and creates habit
- Auto-navigates back to home on success

### **Habit Details Screen** (`HabitDetailsScreen.kt`)
- Edit existing habit title and description
- Toggle completion status
- Update button to save changes
- Delete button (red) to remove habit
- Both actions navigate back to home

---

## 🔄 Data Flow

1. **MainActivity** creates ViewModel via Factory
2. **ViewModel** interacts with DAO for data operations
3. **DAO** performs Room database queries
4. **ViewModel** exposes StateFlow for UI observation
5. **Composables** collect state and render UI
6. **User actions** trigger ViewModel functions
7. **ViewModel** updates database and refreshes state

---

## 📝 Potential Improvements

### **High Priority**
1. **Migration Strategy**: Implement proper Room migrations instead of destructive
2. **Input Validation**: Add validation feedback on habit creation
3. **Loading States**: Show progress indicators during async operations
4. **Empty States**: Display helpful message when no habits exist

### **Medium Priority**
5. **Error Handling**: User-facing error messages for network/database failures
6. **Habit Categories**: Group habits by type (health, productivity, etc.)
7. **Notifications**: Daily reminders for habit completion
8. **Statistics**: Weekly/monthly progress charts

### **Low Priority**
9. **Animations**: Smooth transitions and micro-interactions
10. **Accessibility**: Enhanced screen reader support and content descriptions
11. **Testing**: Unit tests for ViewModel, UI tests for screens
12. **Analytics**: Track user engagement and habit completion rates
13. **Export/Import**: Backup and restore habit data
14. **Widgets**: Home screen widget for quick habit tracking

---

## 🎯 Best Practices Implemented

✅ **Clean Architecture**: Proper separation of concerns (UI, ViewModel, Data)  
✅ **Reactive Programming**: StateFlow for reactive UI updates  
✅ **Dependency Injection**: Factory pattern for ViewModel creation  
✅ **Coroutines**: Proper async/await patterns with viewModelScope  
✅ **Material Design**: Follows Material 3 guidelines  
✅ **Navigation**: Type-safe navigation with arguments  
✅ **State Management**: Unidirectional data flow  
✅ **Resource Management**: Proper use of strings.xml and themes  

---

## 🚀 Conclusion

**HabitForge** is a well-structured, modern Android application that demonstrates best practices in Android development. The codebase is clean, maintainable, and follows the MVVM architecture pattern effectively. The app provides essential habit tracking features with streak tracking and daily motivation, making it a solid foundation for a habit-building application.

The use of Jetpack Compose with Material 3 ensures a modern, responsive UI, while Room database provides reliable local data persistence. The integration of external APIs for motivational quotes adds value to the user experience.

---

*Last Updated: January 1, 2026*
