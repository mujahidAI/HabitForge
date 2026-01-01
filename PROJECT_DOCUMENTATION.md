# HabitForge - Complete Project Documentation

**Version:** 1.0  
**Last Updated:** January 1, 2026  
**Platform:** Android (Kotlin)

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Technical Architecture](#2-technical-architecture)
3. [Detailed Features Documentation](#3-detailed-features-documentation)
4. [Code Logic Explanation](#4-code-logic-explanation)
5. [User Flow & Navigation](#5-user-flow--navigation)
6. [UI/UX Design Decisions](#6-uiux-design-decisions)
7. [Code Files & Their Responsibilities](#7-code-files--their-responsibilities)
8. [Database Schema](#8-database-schema)
9. [Setup & Installation](#9-setup--installation)
10. [Demo Script](#10-demo-script)

---

## 1. Project Overview

### 1.1 Project Name and Description
**HabitForge** is a modern Android habit tracking application that helps users build and maintain positive habits through daily tracking, streak monitoring, and motivational quotes.

### 1.2 Purpose and Goals
**Why HabitForge was created:**
- Help users develop consistent daily habits
- Provide visual feedback on progress through streak tracking
- Motivate users with daily inspirational quotes
- Offer a simple, beautiful interface for habit management

**Problems it solves:**
- Difficulty maintaining consistency with new habits
- Lack of visual progress tracking
- Need for daily motivation and encouragement
- Complexity of existing habit tracking apps

### 1.3 Target Audience
- Individuals seeking personal development
- People building new routines (fitness, reading, meditation, etc.)
- Students developing study habits
- Professionals maintaining work-life balance
- Anyone interested in self-improvement

### 1.4 Key Features Summary
✅ **Create custom habits** with title and description  
✅ **Track daily completion** with intuitive checkboxes  
✅ **Monitor streaks** showing consecutive days of completion  
✅ **Daily motivational quotes** from external API  
✅ **Persistent data storage** using Room database  
✅ **Edit and delete habits** with full CRUD operations  
✅ **Beautiful Material Design 3** UI with custom color scheme  
✅ **Offline functionality** with cached quotes and local database  

---

## 2. Technical Architecture

### 2.1 Technology Stack

**Core Technologies:**
- **Language:** Kotlin 100%
- **Minimum SDK:** API 26 (Android 8.0 Oreo)
- **Target SDK:** Latest Android version
- **Build System:** Gradle

**Android Jetpack Components:**
- **Jetpack Compose** - Modern declarative UI framework
- **Material 3** - Latest Material Design components
- **Room Database** - SQLite abstraction for local data persistence
- **ViewModel** - Lifecycle-aware data management
- **Navigation Compose** - Type-safe screen navigation
- **Coroutines & Flow** - Asynchronous programming and reactive data streams

**Third-Party Libraries:**
- **Retrofit 2** - HTTP client for API calls
- **Gson** - JSON serialization/deserialization
- **ZenQuotes API** - External quote service

### 2.2 Architecture Pattern

**MVVM (Model-View-ViewModel)**

```
┌─────────────────────────────────────────────────┐
│                    VIEW LAYER                    │
│  (Composable UI Screens - HomeScreen, etc.)     │
└──────────────────┬──────────────────────────────┘
                   │ Observes StateFlow
                   │ Calls ViewModel functions
┌──────────────────▼──────────────────────────────┐
│                VIEWMODEL LAYER                   │
│        (HabitViewModel - Business Logic)         │
└──────────────────┬──────────────────────────────┘
                   │ Calls DAO methods
                   │ Manages state
┌──────────────────▼──────────────────────────────┐
│                  MODEL LAYER                     │
│    (Room Database, DAOs, Entities, API Client)   │
└──────────────────────────────────────────────────┘
```

**Why MVVM?**
- Separation of concerns (UI, logic, data)
- Testable business logic
- Lifecycle-aware components
- Reactive UI updates with Flow

### 2.3 Project Structure

```
app/src/main/
├── AndroidManifest.xml
└── java/com/example/habitforge/
    ├── MainActivity.kt                 # App entry point
    ├── data/                           # Data layer
    │   ├── Habit.kt                    # Entity model
    │   ├── HabitDao.kt                 # Database operations
    │   ├── HabitDatabase.kt            # Room database
    │   ├── QuoteApiClient.kt           # Retrofit client
    │   └── QuoteApiService.kt          # API interface
    ├── navigation/
    │   └── AppNavGraph.kt              # Navigation setup
    ├── ui/                             # UI layer
    │   ├── HomeScreen.kt               # Main screen
    │   ├── AddHabitScreen.kt           # Add habit form
    │   ├── HabitDetailsScreen.kt       # Edit/delete screen
    │   └── theme/                      # Design system
    │       ├── Color.kt                # Color palette
    │       ├── Theme.kt                # Theme config
    │       └── Type.kt                 # Typography
    └── viewmodel/                      # ViewModel layer
        ├── HabitViewModel.kt           # Main ViewModel
        ├── HabitViewModelFactory.kt    # Factory pattern
        └── HabitViewModelFactoryProvider.kt
```

---

## 3. Detailed Features Documentation

### Feature 1: Add New Habits

**User Story:**  
*"As a user, I want to create a new habit with a title and description so I can start tracking it."*

**How It Works:**
1. User taps the floating action button (FAB) on home screen
2. Navigation takes user to Add Habit screen
3. User enters habit title (required) and description (optional)
4. User taps "Save Habit" button
5. Habit is saved to database with initial values (streak = 0, isCompleted = false)
6. User returns to home screen where new habit appears

**Implementation Details:**
- **UI:** `AddHabitScreen.kt` - Form with two text fields and save button
- **Logic:** `HabitViewModel.addHabit()` - Creates Habit object and inserts into database
- **Database:** `HabitDao.insertHabit()` - Room insert operation
- **Navigation:** `navController.popBackStack()` - Returns to home after save

**Code Files Involved:**
- `AddHabitScreen.kt` - UI implementation
- `HabitViewModel.kt` - Business logic
- `HabitDao.kt` - Database operation
- `Habit.kt` - Data model

**Visual Flow:**
```
Home Screen → FAB Click → Add Screen → Fill Form → Save → Back to Home
```

---

### Feature 2: View Habit List

**User Story:**  
*"As a user, I want to see all my habits in a scrollable list with their current streaks."*

**How It Works:**
1. App launches and loads `HomeScreen`
2. `LaunchedEffect` triggers `loadHabits()` on ViewModel
3. ViewModel queries database through DAO
4. Habits are emitted via StateFlow
5. UI observes StateFlow and displays habits in LazyColumn
6. Each habit shows: title, streak count, and completion checkbox

**Implementation Details:**
- **UI:** `HomeScreen.kt` - LazyColumn with HabitItem cards
- **Logic:** `HabitViewModel.loadHabits()` - Fetches from database
- **Database:** `HabitDao.getAllHabits()` - SELECT * query
- **State Management:** `StateFlow<List<Habit>>` - Reactive updates

**Code Example:**
```kotlin
fun loadHabits() {
    viewModelScope.launch {
        _habits.value = dao.getAllHabits()
    }
}
```

**Visual Elements:**
- Lavender background (#E5E3EC)
- Cream-colored cards (#F8F8F6) with 24dp rounded corners
- Bold habit title (18sp)
- Light gray streak text (14sp)
- Custom circular checkbox (40dp)

---

### Feature 3: Mark Habits Complete/Incomplete

**User Story:**  
*"As a user, I want to mark a habit as complete for today by tapping the checkbox."*

**How It Works:**
1. User taps the circular checkbox on a habit card
2. `onCheckClick` callback triggers `setCompleted()` in ViewModel
3. ViewModel calculates new streak based on dates
4. Habit is updated in database with new completion status and streak
5. UI automatically refreshes to show updated state
6. Checkbox changes color: Red (incomplete) ↔ Green (complete)

**Implementation Details:**
- **UI:** `CustomCheckbox` composable - Circular button with icons
- **Logic:** `HabitViewModel.setCompleted()` - Streak calculation and update
- **Database:** `HabitDao.updateHabit()` - UPDATE query
- **Visual Feedback:** Color change (ErrorRed ↔ SageGreen), Icon change (X ↔ Check)

**Streak Calculation Algorithm:**
```kotlin
val newStreak = when {
    habit.lastCompletedDate == null && isCompleted -> 1
    habit.lastCompletedDate == yesterday && isCompleted -> habit.streak + 1
    habit.lastCompletedDate == today && isCompleted -> habit.streak
    isCompleted -> 1
    else -> habit.streak
}
```

**Logic Breakdown:**
- **First completion:** Streak = 1
- **Consecutive day:** Increment streak
- **Same day re-check:** Keep current streak
- **Gap in days:** Reset to 1
- **Unchecking:** Maintain current streak

---

### Feature 4: Track Daily Streaks

**User Story:**  
*"As a user, I want to see how many consecutive days I've completed a habit."*

**How It Works:**
1. Each habit stores `streak` (integer) and `lastCompletedDate` (string)
2. When user marks habit complete, system checks date relationship
3. If completed yesterday → increment streak
4. If gap exists → reset to 1
5. Streak displays below habit title: "X day streak"

**Implementation Details:**
- **Data Model:** `Habit.kt` - Contains `streak: Int` and `lastCompletedDate: String?`
- **Logic:** Date comparison using `LocalDate.now()` and `minusDays(1)`
- **Display:** `HabitItem` composable shows streak in light gray text

**Date Handling:**
```kotlin
val today = LocalDate.now().toString()        // "2026-01-01"
val yesterday = LocalDate.now().minusDays(1).toString()  // "2025-12-31"
```

**Edge Cases Handled:**
- First-time completion (null lastCompletedDate)
- Multiple checks same day (no duplicate increment)
- Skipped days (streak resets)
- Unchecking (preserves streak)

---

### Feature 5: Display Inspirational Quotes

**User Story:**  
*"As a user, I want to see a new motivational quote each day to inspire my habit-building journey."*

**How It Works:**
1. App launches and calls `loadDailyQuote()` in ViewModel
2. System checks SharedPreferences for cached quote and date
3. If cached quote is from today → display it (no API call)
4. If new day → fetch fresh quote from ZenQuotes API
5. Quote is displayed at top of home screen in italic serif font
6. Quote and date are saved to SharedPreferences for 24-hour cache

**Implementation Details:**
- **API:** ZenQuotes API (`https://zenquotes.io/api/random`)
- **Client:** Retrofit with Gson converter
- **Caching:** SharedPreferences with date-based validation
- **UI:** Centered text directly on lavender background
- **Fallback:** Default quotes if API fails or no internet

**Code Flow:**
```kotlin
fun loadDailyQuote() {
    viewModelScope.launch {
        val savedQuote = prefs.getString("quote_text", null)
        val savedDate = prefs.getString("quote_date", null)
        val today = LocalDate.now().toString()

        if (savedQuote != null && savedDate == today) {
            _quote.value = savedQuote  // Use cached
            return@launch
        }

        try {
            val response = QuoteApiClient.api.getQuote()
            val newQuote = response.firstOrNull()?.q ?: "Stay consistent..."
            _quote.value = newQuote
            
            // Cache for 24h
            prefs.edit()
                .putString("quote_text", newQuote)
                .putString("quote_date", today)
                .apply()
        } catch (e: Exception) {
            _quote.value = "Believe in yourself. Progress is progress 💪"
        }
    }
}
```

**Benefits:**
- Saves battery (one API call per day)
- Works offline (cached quote)
- Reduces data usage
- Consistent daily motivation

---

### Feature 6: Data Persistence

**User Story:**  
*"As a user, I want my habits and progress to be saved so they're available when I reopen the app."*

**How It Works:**
1. All habit data stored in Room SQLite database
2. Database persists on device storage
3. Data survives app closures and device restarts
4. Quotes cached in SharedPreferences
5. No cloud sync (fully local)

**Implementation Details:**
- **Database:** Room with SQLite backend
- **Location:** App's private storage directory
- **Persistence:** Automatic with Room
- **Backup:** Can be configured via Android backup rules

**Database Configuration:**
```kotlin
Room.databaseBuilder(
    context.applicationContext,
    HabitDatabase::class.java,
    "habit_database"
)
.fallbackToDestructiveMigration()
.build()
```

**Data Lifecycle:**
- **Create:** Insert new habit → saved immediately
- **Read:** Query on app launch → loads all habits
- **Update:** Modify habit → changes persisted
- **Delete:** Remove habit → permanently deleted

---

### Feature 7: Edit and Delete Habits

**User Story:**  
*"As a user, I want to edit habit details or delete habits I no longer need."*

**How It Works:**
1. User taps on a habit card in the list
2. Navigation passes habit ID to details screen
3. Details screen loads habit data from database
4. User can edit title, description, or completion status
5. "Update Habit" button saves changes to database
6. "Delete Habit" button removes habit permanently
7. User returns to home screen

**Implementation Details:**
- **UI:** `HabitDetailsScreen.kt` - Form with update/delete buttons
- **Navigation:** `details/{habitId}` route with Int parameter
- **Logic:** `updateHabit()` and `deleteHabit()` in ViewModel
- **Database:** UPDATE and DELETE SQL operations via DAO

**Code Example:**
```kotlin
// Update
fun updateHabit(habit: Habit) {
    viewModelScope.launch {
        dao.updateHabit(habit)
        loadHabits()
    }
}

// Delete
fun deleteHabit(habit: Habit) {
    viewModelScope.launch {
        dao.deleteHabit(habit)
        loadHabits()
    }
}
```

---

## 4. Code Logic Explanation

### 4.1 Data Models

**Habit Entity:**
```kotlin
@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val streak: Int = 0,
    val lastCompletedDate: String? = null
)
```

**Field Explanations:**
- `id`: Auto-generated primary key (unique identifier)
- `title`: Habit name (e.g., "Morning Exercise")
- `description`: Optional details (e.g., "30 min cardio")
- `isCompleted`: Today's completion status (true/false)
- `streak`: Consecutive days completed (integer)
- `lastCompletedDate`: ISO date string (e.g., "2026-01-01")

**Quote Response Model:**
```kotlin
data class QuoteResponse(
    val q: String,  // quote text
    val a: String   // author
)
```

### 4.2 Database Operations

**DAO Interface:**
```kotlin
@Dao
interface HabitDao {
    @Query("SELECT * FROM habits")
    suspend fun getAllHabits(): List<Habit>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)
    
    @Update
    suspend fun updateHabit(habit: Habit)
    
    @Delete
    suspend fun deleteHabit(habit: Habit)
    
    @Query("SELECT * FROM habits WHERE id = :habitId LIMIT 1")
    suspend fun getHabitById(habitId: Int): Habit?
}
```

**How Data Persists:**
1. **Room** generates SQL queries from annotations
2. **SQLite** stores data in device's internal storage
3. **Coroutines** handle async operations (suspend functions)
4. **ViewModel** manages data lifecycle
5. **StateFlow** emits updates to UI

**Streak Calculation Logic:**

```kotlin
@RequiresApi(26)
fun setCompleted(habitId: Int, isCompleted: Boolean) {
    viewModelScope.launch {
        val habit = dao.getHabitById(habitId) ?: return@launch
        
        val today = LocalDate.now().toString()
        val yesterday = LocalDate.now().minusDays(1).toString()
        
        val newStreak = when {
            // First time completing
            habit.lastCompletedDate == null && isCompleted -> 1
            
            // Consecutive day (yesterday → today)
            habit.lastCompletedDate == yesterday && isCompleted -> habit.streak + 1
            
            // Same day (already completed today)
            habit.lastCompletedDate == today && isCompleted -> habit.streak
            
            // Gap in days (reset streak)
            isCompleted -> 1
            
            // Unchecking (keep current streak)
            else -> habit.streak
        }
        
        val updatedHabit = habit.copy(
            isCompleted = isCompleted,
            streak = newStreak,
            lastCompletedDate = if (isCompleted) today else habit.lastCompletedDate
        )
        
        dao.updateHabit(updatedHabit)
        loadHabits()
    }
}
```

**Step-by-Step Breakdown:**
1. Retrieve habit from database by ID
2. Get today's date and yesterday's date
3. Compare `lastCompletedDate` with today/yesterday
4. Calculate new streak based on date relationship
5. Create updated habit with new values
6. Save to database
7. Refresh UI by reloading habits

### 4.3 UI Components

**LazyColumn for Habit List:**
```kotlin
LazyColumn(
    verticalArrangement = Arrangement.spacedBy(14.dp)
) {
    items(habits) { habit ->
        HabitItem(
            habit = habit,
            onClick = { navController.navigate("details/${habit.id}") },
            onCheckClick = { isChecked -> viewModel.setCompleted(habit.id, isChecked) }
        )
    }
}
```

**Benefits:**
- Lazy loading (only renders visible items)
- Efficient scrolling for large lists
- Automatic recycling of views
- Built-in spacing with `Arrangement.spacedBy()`

**Custom Checkbox Implementation:**
```kotlin
@Composable
fun CustomCheckbox(
    isChecked: Boolean,
    onCheckedChange: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(40.dp)
            .clickable { onCheckedChange() },
        shape = CircleShape,
        color = if (isChecked) SageGreen else ErrorRed
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = if (isChecked) Icons.Default.Check else Icons.Default.Close,
                contentDescription = if (isChecked) "Completed" else "Not Completed",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
```

**State Management:**
- `isChecked` determines color and icon
- `onCheckedChange` callback updates ViewModel
- Reactive UI updates via StateFlow observation

### 4.4 Business Logic

**ViewModel Initialization:**
```kotlin
class HabitViewModel(
    private val dao: HabitDao,
    application: Application
) : AndroidViewModel(application) {
    
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits = _habits.asStateFlow()
    
    private val prefs = getApplication<Application>()
        .getSharedPreferences("daily_quote", Context.MODE_PRIVATE)
    
    private val _quote = MutableStateFlow("Loading motivational quote...")
    val quote = _quote.asStateFlow()
}
```

**Factory Pattern for ViewModel:**
```kotlin
class HabitViewModelFactory(
    private val dao: HabitDao,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            return HabitViewModel(dao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

**Why Factory Pattern?**
- ViewModel needs DAO dependency
- Can't use default constructor
- Factory provides dependencies at creation
- Ensures single ViewModel instance per activity

---

## 5. User Flow & Navigation

### 5.1 Complete User Journey

**First Launch:**
```
1. App opens → MainActivity loads
2. ViewModel initialized with Factory
3. HomeScreen displayed with lavender background
4. loadHabits() called → empty list (first time)
5. loadDailyQuote() called → fetches from API
6. Quote displays at top in italic serif
7. Empty habit list (no cards shown)
8. FAB visible in bottom-right corner
```

**Adding First Habit:**
```
1. User taps FAB (+)
2. Navigate to AddHabitScreen
3. User types "Morning Meditation" (title)
4. User types "10 minutes mindfulness" (description)
5. User taps "Save Habit"
6. ViewModel.addHabit() creates Habit object
7. DAO inserts into database
8. Navigate back to HomeScreen
9. loadHabits() refreshes list
10. New habit card appears with 0 day streak
11. Red checkbox (unchecked state)
```

**Completing a Habit:**
```
1. User taps red checkbox on "Morning Meditation"
2. onCheckClick triggers setCompleted(habitId, true)
3. ViewModel retrieves habit from database
4. Calculates streak (first time = 1)
5. Updates habit: isCompleted=true, streak=1, lastCompletedDate="2026-01-01"
6. Saves to database
7. loadHabits() refreshes UI
8. Checkbox turns green with checkmark
9. Streak updates to "1 day streak"
```

**Next Day Flow:**
```
1. User opens app on January 2
2. loadDailyQuote() checks cache
3. Cached date = "2026-01-01", today = "2026-01-02"
4. Dates don't match → fetch new quote from API
5. New quote cached with today's date
6. Habits load (isCompleted still true from yesterday)
7. User taps green checkbox to uncheck
8. Checkbox turns red (not completed today)
9. User taps red checkbox to complete today
10. System compares: lastCompletedDate="2026-01-01", yesterday="2026-01-01"
11. Consecutive day detected → streak increments to 2
12. Checkbox turns green, displays "2 day streak"
```

**Editing a Habit:**
```
1. User taps on "Morning Meditation" card
2. Navigate to HabitDetailsScreen with habitId
3. Screen loads habit data from database
4. Text fields pre-filled with current values
5. User changes title to "Morning Meditation & Yoga"
6. User taps "Update Habit"
7. ViewModel.updateHabit() saves changes
8. Navigate back to HomeScreen
9. Updated title displays in list
```

**Deleting a Habit:**
```
1. User taps on habit card
2. Navigate to HabitDetailsScreen
3. User taps red "Delete Habit" button
4. ViewModel.deleteHabit() removes from database
5. Navigate back to HomeScreen
6. Habit no longer appears in list
```

### 5.2 Navigation Graph

```
MainActivity (Entry Point)
    │
    └─→ AppNavGraph (Navigation Host)
            │
            ├─→ "home" → HomeScreen
            │       │
            │       ├─→ FAB Click → "add"
            │       └─→ Card Click → "details/{habitId}"
            │
            ├─→ "add" → AddHabitScreen
            │       │
            │       └─→ Save → popBackStack() → "home"
            │
            └─→ "details/{habitId}" → HabitDetailsScreen
                    │
                    ├─→ Update → popBackStack() → "home"
                    └─→ Delete → popBackStack() → "home"
```

**Navigation Implementation:**
```kotlin
NavHost(
    navController = navController,
    startDestination = "home"
) {
    composable("home") {
        HomeScreen(navController, viewModel)
    }
    
    composable("add") {
        AddHabitScreen(navController, viewModel)
    }
    
    composable(
        route = "details/{habitId}",
        arguments = listOf(navArgument("habitId") { type = NavType.IntType })
    ) { backStackEntry ->
        val habitId = backStackEntry.arguments?.getInt("habitId") ?: 0
        HabitDetailsScreen(navController, habitId, viewModel)
    }
}
```

### 5.3 Data Flow Diagram

```
┌──────────────┐
│     USER     │
└──────┬───────┘
       │ Interaction (tap, type, etc.)
       ▼
┌──────────────────────────────────┐
│      UI LAYER (Composables)      │
│  HomeScreen, AddHabitScreen, etc.│
└──────┬───────────────────────────┘
       │ Calls ViewModel functions
       │ Observes StateFlow
       ▼
┌──────────────────────────────────┐
│   VIEWMODEL (Business Logic)     │
│      HabitViewModel.kt            │
└──────┬───────────────────────────┘
       │ Calls DAO methods
       │ Manages coroutines
       ▼
┌──────────────────────────────────┐
│      DAO (Data Access)            │
│        HabitDao.kt                │
└──────┬───────────────────────────┘
       │ SQL queries
       ▼
┌──────────────────────────────────┐
│   ROOM DATABASE (SQLite)          │
│     HabitDatabase.kt              │
└──────┬───────────────────────────┘
       │ Persists to disk
       ▼
┌──────────────────────────────────┐
│    DEVICE STORAGE (SQLite file)   │
└───────────────────────────────────┘
```

---

## 6. UI/UX Design Decisions

### 6.1 Color Palette

**Primary Colors:**
```kotlin
val LavenderBackground = Color(0xFFE5E3EC)  // Light purple-gray
val CreamCard = Color(0xFFF8F8F6)           // Off-white/cream
val SageGreen = Color(0xFF9BB090)           // Sage green
val ErrorRed = Color(0xFFE74C3C)            // Red
val LightGray = Color(0xFFB0B0B0)           // Light gray
val TextPrimary = Color(0xFF000000)         // Black
```

**Color Psychology & Reasoning:**
- **Lavender Background:** Calming, promotes focus and mindfulness (perfect for habit-building)
- **Cream Cards:** Soft, non-distracting, easy on eyes for extended use
- **Sage Green:** Success, growth, positive reinforcement (completed habits)
- **Red:** Attention, urgency, incomplete status (motivates action)
- **Light Gray:** Subtle, secondary information (streaks)

**Accessibility:**
- High contrast between text and backgrounds
- Color-blind friendly (green/red + icon differentiation)
- WCAG AA compliant for text readability

### 6.2 Typography

**Font Choices:**
```kotlin
// Quote text
bodyMedium = TextStyle(
    fontFamily = FontFamily.Serif,
    fontStyle = FontStyle.Italic,
    fontSize = 20.sp,
    lineHeight = 28.sp
)

// Habit title
titleMedium = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp
)

// Streak text
bodySmall = TextStyle(
    fontSize = 14.sp,
    color = LightGray
)
```

**Typography Reasoning:**
- **Serif Italic for Quotes:** Elegant, literary feel, distinguishes quotes from UI text
- **Bold Sans-Serif for Titles:** Clear, modern, easy to scan
- **Small Light Gray for Streaks:** De-emphasized but visible, doesn't compete with title

### 6.3 Layout Principles

**Spacing System:**
- **4dp base unit** for consistency
- **Card spacing:** 14dp between items
- **Card padding:** 20dp horizontal, 16dp vertical
- **Screen margins:** 24dp horizontal, 32dp vertical
- **Corner radius:** 24dp for cards, 16dp for containers

**Visual Hierarchy:**
1. **Quote** - Top, centered, largest text
2. **Habit Cards** - Primary content, bold titles
3. **Streaks** - Secondary info, smaller text
4. **Checkboxes** - Action items, color-coded

**Gestalt Principles Applied:**
- **Proximity:** Related items grouped (title + streak)
- **Similarity:** All habit cards look similar (consistency)
- **Enclosure:** Cards contain related information
- **Figure-Ground:** Cards stand out from background

### 6.4 Material Design Compliance

**Material 3 Components Used:**
- `Surface` - Cards with elevation
- `FloatingActionButton` - Primary action
- `Button` - Secondary actions
- `OutlinedTextField` - Form inputs
- `Checkbox` - Standard checkboxes (details screen)
- `LazyColumn` - Scrollable lists
- `Icon` - Vector graphics

**Material Design Principles:**
- **Material surfaces:** Cards with shadows for depth
- **Elevation:** Subtle shadows (2-4dp)
- **Motion:** Smooth navigation transitions
- **Responsive:** Adapts to different screen sizes
- **Touch targets:** 40dp minimum (checkboxes)

### 6.5 User Experience Decisions

**Intuitive Interactions:**
- **Tap card → Edit:** Natural expectation
- **Tap checkbox → Toggle:** Immediate feedback
- **FAB for add:** Standard Android pattern
- **Color coding:** Green = done, Red = todo

**Feedback Mechanisms:**
- **Visual:** Color changes on checkbox toggle
- **Immediate:** No loading delays for local operations
- **Clear:** Obvious what each button does

**Error Prevention:**
- **Required fields:** Title must be filled
- **Confirmation:** Delete is clearly labeled in red
- **Reversible:** Can uncheck habits if mistakenly checked

---

## 7. Code Files & Their Responsibilities

### 7.1 Main Application Files

| File | Purpose | Key Methods/Functions | Dependencies |
|------|---------|----------------------|--------------|
| **MainActivity.kt** | App entry point, initializes ViewModel | `onCreate()` | HabitViewModel, AppNavGraph |
| **AndroidManifest.xml** | App configuration, permissions | N/A | N/A |

### 7.2 Data Layer Files

| File | Purpose | Key Methods/Functions | Dependencies |
|------|---------|----------------------|--------------|
| **Habit.kt** | Entity model for habits | N/A (data class) | Room annotations |
| **HabitDao.kt** | Database operations interface | `getAllHabits()`, `insertHabit()`, `updateHabit()`, `deleteHabit()`, `getHabitById()` | Habit entity |
| **HabitDatabase.kt** | Room database configuration | `getDatabase()`, `habitDao()` | HabitDao, Habit |
| **QuoteApiClient.kt** | Retrofit client singleton | `api` property | Retrofit, Gson |
| **QuoteApiService.kt** | API interface definition | `getQuote()` | Retrofit annotations |

### 7.3 UI Layer Files

| File | Purpose | Key Methods/Functions | Dependencies |
|------|---------|----------------------|--------------|
| **HomeScreen.kt** | Main screen with habit list | `HomeScreen()`, `HabitItem()`, `CustomCheckbox()` | HabitViewModel, NavController |
| **AddHabitScreen.kt** | Form to create new habits | `AddHabitScreen()` | HabitViewModel, NavController |
| **HabitDetailsScreen.kt** | Edit/delete existing habits | `HabitDetailsScreen()` | HabitViewModel, NavController |
| **Color.kt** | Color palette definitions | N/A (constants) | Compose Color |
| **Theme.kt** | Theme configuration | `HabitForgeTheme()` | Material 3 theme |
| **Type.kt** | Typography definitions | N/A (constants) | Material 3 typography |

### 7.4 ViewModel Layer Files

| File | Purpose | Key Methods/Functions | Dependencies |
|------|---------|----------------------|--------------|
| **HabitViewModel.kt** | Business logic and state management | `loadHabits()`, `addHabit()`, `updateHabit()`, `deleteHabit()`, `setCompleted()`, `loadDailyQuote()` | HabitDao, QuoteApiClient |
| **HabitViewModelFactory.kt** | Factory for ViewModel creation | `create()` | HabitDao, Application |
| **HabitViewModelFactoryProvider.kt** | Provides factory instance | `provide()` | HabitDatabase, HabitViewModelFactory |

### 7.5 Navigation Files

| File | Purpose | Key Methods/Functions | Dependencies |
|------|---------|----------------------|--------------|
| **AppNavGraph.kt** | Navigation setup and routes | `AppNavGraph()` | All screen composables, HabitViewModel |

### 7.6 Detailed File Breakdown

#### **HabitViewModel.kt** (Most Complex File)

**Responsibilities:**
- Manage habit list state
- Handle CRUD operations
- Calculate streaks
- Fetch and cache daily quotes
- Coordinate between UI and data layer

**Key Functions:**

1. **loadHabits()**
   - Fetches all habits from database
   - Updates StateFlow for UI observation
   - Called on app launch and after modifications

2. **addHabit(title, description)**
   - Creates new Habit object
   - Inserts into database via DAO
   - Refreshes habit list

3. **updateHabit(habit)**
   - Updates existing habit in database
   - Refreshes habit list

4. **deleteHabit(habit)**
   - Removes habit from database
   - Refreshes habit list

5. **setCompleted(habitId, isCompleted)**
   - Retrieves habit by ID
   - Calculates new streak based on dates
   - Updates completion status and streak
   - Saves to database

6. **loadDailyQuote()**
   - Checks SharedPreferences cache
   - Fetches from API if new day
   - Handles errors with fallback quotes
   - Caches quote for 24 hours

**Dependencies:**
- `HabitDao` - Database operations
- `Application` - Context for SharedPreferences
- `QuoteApiClient` - API calls
- `viewModelScope` - Coroutine scope
- `StateFlow` - Reactive state

---

## 8. Database Schema

### 8.1 Table Structure

**Table Name:** `habits`

| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| `id` | INTEGER | PRIMARY KEY, AUTOINCREMENT | Unique identifier |
| `title` | TEXT | NOT NULL | Habit name |
| `description` | TEXT | NOT NULL | Habit details |
| `isCompleted` | INTEGER | NOT NULL, DEFAULT 0 | Boolean (0=false, 1=true) |
| `streak` | INTEGER | NOT NULL, DEFAULT 0 | Consecutive days count |
| `lastCompletedDate` | TEXT | NULLABLE | ISO date string |

### 8.2 SQL Schema

```sql
CREATE TABLE habits (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    isCompleted INTEGER NOT NULL DEFAULT 0,
    streak INTEGER NOT NULL DEFAULT 0,
    lastCompletedDate TEXT
);
```

### 8.3 Sample Data

**Example 1: New Habit**
```json
{
    "id": 1,
    "title": "Morning Exercise",
    "description": "30 minutes cardio",
    "isCompleted": false,
    "streak": 0,
    "lastCompletedDate": null
}
```

**Example 2: Active Streak**
```json
{
    "id": 2,
    "title": "Read for 20 minutes",
    "description": "Fiction or non-fiction",
    "isCompleted": true,
    "streak": 7,
    "lastCompletedDate": "2026-01-01"
}
```

**Example 3: Broken Streak**
```json
{
    "id": 3,
    "title": "Meditation",
    "description": "10 minutes mindfulness",
    "isCompleted": false,
    "streak": 1,
    "lastCompletedDate": "2025-12-30"
}
```

### 8.4 Database Operations

**Insert:**
```kotlin
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun insertHabit(habit: Habit)
```
- Adds new habit to database
- Replaces if ID already exists
- Auto-generates ID if not provided

**Query All:**
```kotlin
@Query("SELECT * FROM habits")
suspend fun getAllHabits(): List<Habit>
```
- Returns all habits
- Ordered by insertion (ID)

**Query by ID:**
```kotlin
@Query("SELECT * FROM habits WHERE id = :habitId LIMIT 1")
suspend fun getHabitById(habitId: Int): Habit?
```
- Returns single habit or null
- Used for streak calculations

**Update:**
```kotlin
@Update
suspend fun updateHabit(habit: Habit)
```
- Updates existing habit by ID
- All fields updated

**Delete:**
```kotlin
@Delete
suspend fun deleteHabit(habit: Habit)
```
- Removes habit by ID
- Permanent deletion

### 8.5 Database Versioning

**Current Version:** 2

**Migration Strategy:** Destructive
```kotlin
.fallbackToDestructiveMigration()
```
- Schema changes delete existing data
- Suitable for development
- Production apps should implement proper migrations

**Version History:**
- **Version 1:** Initial schema (title, description, isCompleted)
- **Version 2:** Added streak and lastCompletedDate fields

---

## 9. Setup & Installation

### 9.1 Prerequisites

**Required Software:**
- **Android Studio:** Hedgehog (2023.1.1) or later
- **JDK:** Java 17 or higher
- **Android SDK:** API 26 (Android 8.0) minimum
- **Gradle:** 8.0+ (included with Android Studio)

**Recommended:**
- **Physical Device or Emulator:** API 26+ for testing
- **Git:** For version control
- **Internet Connection:** For API quote fetching

### 9.2 Installation Steps

**Step 1: Clone/Download Project**
```bash
# If using Git
git clone <repository-url>
cd HabitForge

# Or download ZIP and extract
```

**Step 2: Open in Android Studio**
1. Launch Android Studio
2. Click "Open" or "Open an Existing Project"
3. Navigate to HabitForge folder
4. Click "OK"
5. Wait for Gradle sync to complete

**Step 3: Verify Configuration**
1. Check `build.gradle` (Module: app):
   ```gradle
   android {
       compileSdk 34
       defaultConfig {
           minSdk 26
           targetSdk 34
       }
   }
   ```

2. Ensure dependencies are resolved:
   - Room
   - Retrofit
   - Compose
   - Material 3

**Step 4: Sync Gradle**
1. Click "Sync Now" if prompted
2. Or: File → Sync Project with Gradle Files
3. Wait for sync to complete

**Step 5: Connect Device/Emulator**
- **Physical Device:**
  1. Enable Developer Options
  2. Enable USB Debugging
  3. Connect via USB
  4. Allow USB debugging on device

- **Emulator:**
  1. Tools → Device Manager
  2. Create Virtual Device
  3. Select device (e.g., Pixel 6)
  4. Select system image (API 26+)
  5. Click "Finish"

**Step 6: Build and Run**
1. Click green "Run" button (▶)
2. Select target device
3. Wait for build to complete
4. App launches on device

### 9.3 Troubleshooting

**Common Issues:**

**Issue 1: Gradle Sync Failed**
- **Solution:** File → Invalidate Caches → Invalidate and Restart

**Issue 2: SDK Not Found**
- **Solution:** Tools → SDK Manager → Install required SDK versions

**Issue 3: Build Errors**
- **Solution:** Build → Clean Project → Rebuild Project

**Issue 4: App Crashes on Launch**
- **Solution:** Check Logcat for errors, ensure API 26+ device

### 9.4 Configuration

**No configuration needed!** The app works out of the box with:
- ✅ Pre-configured Room database
- ✅ API client ready (ZenQuotes)
- ✅ Default theme and colors
- ✅ All permissions set in manifest

**Optional Customization:**
- Change colors in `Color.kt`
- Modify API endpoint in `QuoteApiClient.kt`
- Adjust database name in `HabitDatabase.kt`

---

## 10. Demo Script

### 10.1 Presentation Sequence

**Opening (30 seconds)**
> "Today I'm presenting **HabitForge**, a modern Android habit tracking application built with Kotlin and Jetpack Compose. This app helps users build consistent daily habits through visual progress tracking, streak monitoring, and daily motivation."

**Feature 1: Daily Motivation (1 minute)**
> "When you open the app, you're greeted with an inspirational quote at the top. This quote is fetched from the ZenQuotes API and cached for 24 hours. Notice the elegant italic serif font - this creates a literary, motivational feel. The quote changes automatically each day to keep users inspired."

**Demo Action:**
- Point to quote on screen
- Explain: "This quote is live from an external API"
- Mention: "Tomorrow, a new quote will appear automatically"

**Feature 2: Habit List & UI Design (1 minute)**
> "Below the quote, we have the habit list. Notice the beautiful lavender background and cream-colored cards with rounded corners. This color scheme was chosen for its calming effect, perfect for habit-building. Each card shows the habit title in bold, the current streak count, and a custom circular checkbox."

**Demo Action:**
- Scroll through habit list
- Point out: "Clean, modern Material Design 3"
- Highlight: "Consistent spacing and typography"

**Feature 3: Adding a Habit (1 minute)**
> "To add a new habit, tap the floating action button in the bottom-right. This follows Android's Material Design guidelines for primary actions. Let me create a habit called 'Drink 8 Glasses of Water' with a description."

**Demo Action:**
- Tap FAB
- Type: "Drink 8 Glasses of Water"
- Type description: "Stay hydrated throughout the day"
- Tap "Save Habit"
- Show: New habit appears in list

**Feature 4: Streak Tracking (2 minutes)**
> "Now for the most impressive feature - streak tracking. Watch what happens when I mark this habit as complete."

**Demo Action:**
- Tap red checkbox
- Point out: "Checkbox turns green with a checkmark"
- Show: "Streak updates to '1 day streak'"
- Explain: "The app stores today's date as the last completed date"

> "Here's where it gets interesting. The streak calculation uses date comparison logic. If I complete this habit tomorrow, the app will detect it's a consecutive day and increment the streak to 2. If I skip a day, the streak resets to 1. This encourages daily consistency."

**Technical Highlight:**
> "Behind the scenes, this uses LocalDate API to compare dates. The algorithm checks if the last completed date was yesterday - if so, it increments the streak. If there's a gap, it resets. All of this is calculated in the ViewModel and persisted to the Room database."

**Feature 5: Data Persistence (1 minute)**
> "All habit data is stored locally using Room, which is Google's recommended SQLite abstraction. This means your habits and streaks are saved permanently on your device. Let me demonstrate by closing the app completely and reopening it."

**Demo Action:**
- Close app (swipe away from recents)
- Reopen app
- Show: All habits still there with correct streaks

> "Notice everything is exactly as we left it. The database persists across app sessions, and the quote is still cached from earlier today."

**Feature 6: Editing & Deleting (1 minute)**
> "To edit a habit, simply tap on the card. This takes you to the details screen where you can modify the title, description, or delete the habit entirely."

**Demo Action:**
- Tap on a habit card
- Show edit screen
- Change title slightly
- Tap "Update Habit"
- Show: Updated title in list

**Feature 7: Architecture (1 minute)**
> "From a technical standpoint, this app follows the MVVM architecture pattern. The UI layer is built with Jetpack Compose - Google's modern declarative UI framework. The ViewModel manages business logic and state. The data layer uses Room for local persistence and Retrofit for API calls."

**Show Architecture Diagram (if available):**
> "Data flows from the UI to the ViewModel, then to the DAO, and finally to the SQLite database. State flows back up through StateFlow, which automatically updates the UI reactively."

**Feature 8: API Integration (30 seconds)**
> "The quote feature demonstrates real-world API integration. We use Retrofit with Gson for JSON parsing. The app handles errors gracefully - if the API is down or there's no internet, it shows a fallback motivational quote."

### 10.2 Key Talking Points

**Technical Highlights:**
1. **100% Kotlin** - Modern, concise, type-safe
2. **Jetpack Compose** - Declarative UI, less boilerplate
3. **Material Design 3** - Latest design system
4. **MVVM Architecture** - Clean separation of concerns
5. **Room Database** - Type-safe SQL queries
6. **Coroutines & Flow** - Asynchronous programming
7. **Retrofit** - Industry-standard HTTP client
8. **Factory Pattern** - Proper dependency injection

**Impressive Details:**
- ✨ Automatic daily quote refresh
- ✨ Intelligent streak calculation with date logic
- ✨ Offline functionality with caching
- ✨ Custom UI components (circular checkboxes)
- ✨ Reactive UI updates with StateFlow
- ✨ Persistent data across app sessions
- ✨ Clean, modern design aesthetic

### 10.3 Q&A Preparation

**Expected Questions & Answers:**

**Q: How does the streak calculation work?**
> "The app stores the last completed date as an ISO string. When the user marks a habit complete, it compares this date with yesterday's date using LocalDate API. If they match, the streak increments. If there's a gap, it resets to 1. This ensures accurate consecutive day tracking."

**Q: What happens if the user loses internet connection?**
> "The app works fully offline. Habits are stored locally in Room database. The quote is cached for 24 hours in SharedPreferences. If the API fails, we show a fallback quote. Only the daily quote refresh requires internet."

**Q: Why did you choose MVVM architecture?**
> "MVVM provides clear separation between UI, business logic, and data. The ViewModel survives configuration changes like screen rotation. StateFlow enables reactive UI updates. It's also Google's recommended architecture for Android apps."

**Q: How is data persisted?**
> "We use Room, which is an abstraction over SQLite. Room provides compile-time verification of SQL queries, type-safe database access, and seamless integration with coroutines. All habit data is stored in a local SQLite database on the device."

**Q: What libraries did you use?**
> "Core libraries include Jetpack Compose for UI, Room for database, Retrofit for networking, and Gson for JSON parsing. We also use Kotlin Coroutines for async operations and StateFlow for reactive state management."

**Q: How did you implement the custom checkboxes?**
> "The checkboxes are custom Composable functions. They use a Surface with CircleShape, conditional colors (green for checked, red for unchecked), and Material Icons (Check vs Close). The state is managed by the ViewModel and updates reactively."

**Q: Can users track multiple habits?**
> "Yes! Users can create unlimited habits. Each habit is stored as a separate row in the database with its own ID, title, description, streak, and completion status. The LazyColumn efficiently handles large lists."

**Q: What's the minimum Android version?**
> "API 26 (Android 8.0 Oreo) because we use the LocalDate API for date calculations. This covers over 95% of active Android devices."

### 10.4 Closing Statement

> "In summary, HabitForge demonstrates modern Android development best practices with Kotlin, Jetpack Compose, MVVM architecture, and Room database. It solves a real-world problem - helping people build consistent habits - with an intuitive, beautiful interface and intelligent streak tracking. The app is fully functional, offline-capable, and ready for real-world use. Thank you for your time, and I'm happy to answer any questions."

---

## Appendix: Quick Reference

### Key Metrics
- **Lines of Code:** ~600 (excluding generated files)
- **Number of Screens:** 3 (Home, Add, Details)
- **Database Tables:** 1 (habits)
- **API Endpoints:** 1 (ZenQuotes random quote)
- **Third-Party Libraries:** 4 (Room, Retrofit, Gson, Compose)

### File Count by Layer
- **UI Layer:** 6 files (3 screens + 3 theme files)
- **ViewModel Layer:** 3 files
- **Data Layer:** 5 files
- **Navigation:** 1 file
- **Total:** 15 Kotlin files

### Color Reference
```
Lavender: #E5E3EC
Cream: #F8F8F6
Sage Green: #9BB090
Red: #E74C3C
Light Gray: #B0B0B0
Dark Gray: #2C2C2C
```

### Important Dates & Versions
- **Room Database Version:** 2
- **Minimum SDK:** 26
- **Target SDK:** 34
- **Kotlin Version:** Latest stable

---

**End of Documentation**

*This documentation is maintained for the HabitForge project. For questions or updates, please contact the development team.*
