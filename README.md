# 📊 Fund Explorer - Mutual Funds Discovery App

A modern Android application built with Jetpack Compose that allows users to explore, search, and analyze mutual funds using the [MFApi.in](https://www.mfapi.in) public API.

---
## 🎯 Key Features

### ✅ Implemented
1. **Browse Top Mutual Funds** - Curated list from popular fund houses
2. **Search Functionality** - Real-time search with debouncing
3. **Category Filtering** - Filter by Equity, Debt, Hybrid, Solution, Other
4. **Fund Details View** - Complete information with NAV history
5. **Graphical NAV Trends** - Custom line chart with time range selection (1M, 3M, 6M, 1Y)
6. **Performance Statistics** - Min/Max NAV, Average, Returns calculation
7. **In-Memory Caching** - 5-minute cache for better performance
8. **Offline Support** - Show cached data during network failures
9. **Back Navigation** - Intelligent back handling (clears search before exit)
10. **Error Handling** - Graceful error messages with retry options
11. **Loading States** - Smooth loading indicators
12. **Empty States** - Helpful messages when no data found


---
## 📱 Technical Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Kotlin | 1.9.10 |
| UI Framework | Jetpack Compose | 1.5.4 |
| Design System | Material 3 | 1.1.2 |
| Architecture | Clean + MVVM | - |
| DI | Hilt | 2.48 |
| Networking | Retrofit | 2.9.0 |
| JSON Parsing | Gson | 2.9.0 |
| Async | Coroutines + Flow | 1.7.3 |
| Navigation | Navigation Compose | 2.7.5 |
| Min SDK | Android 7.0 | API 24 |
| Target SDK | Android 14 | API 34 |

---
## 📸 Screenshots

<div align="center">


### LightMode
<table>

  <tr>
    <td><img src="https://github.com/user-attachments/assets/486f819a-2629-4a29-8d88-11241a13913d" alt="Home" width="250"/><br/><b>Home</b></td>
    <td><img src="https://github.com/user-attachments/assets/e39beb40-dec7-406e-abe8-6f7a66b96e43" alt="Detail" width="250"/><br/><b>Detail</b></td>
  </tr>
</table>

### DarkMode
<table>

  <tr>
    <td><img src="https://github.com/user-attachments/assets/a6275c54-e998-49bf-a7ce-e4c6a8db8fe6" alt="Home" width="250"/><br/><b>Home</b></td>
    <td><img src="https://github.com/user-attachments/assets/a83a9db7-7416-4bdb-be1d-ee00e038e44d" alt="Detail" width="250"/><br/><b>Detail</b></td>
  </tr>
</table>
</div>


---
## 🚀 How to Run the App

### Prerequisites
- **Android Studio**: Hedgehog (2023.1.1) or later
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **JDK**: Java 8 or higher
- **Internet Connection**: Required for fetching mutual fund data

### Steps to Run

1. **Clone or Download the Project**
   ```bash
   git clone <repository-url>
   cd fund-explorer
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the project folder and open it

3. **Sync Gradle**
   - Android Studio will automatically prompt to sync Gradle
   - Wait for the sync to complete
   - If not automatic, click `File > Sync Project with Gradle Files`

4. **Build the Project**
   - Click `Build > Make Project` or press `Ctrl+F9` (Windows/Linux) / `Cmd+F9` (Mac)
   - Wait for the build to complete successfully

5. **Run the App**
   - Connect an Android device via USB (with USB Debugging enabled) OR start an Android Emulator
   - Click the "Run" button (green triangle) or press `Shift+F10`
   - Select your target device
   - The app will install and launch automatically

### Troubleshooting
- **Build Errors**: Ensure you have the correct JDK version and all dependencies are downloaded
- **Network Issues**: Check internet connection for API access
- **Emulator Issues**: Use a device with API 24 or higher

---

## 🏗️ Architectural Decisions

### 1. **Clean Architecture**
The app follows Clean Architecture principles with clear separation of concerns:

```
📦 app
├── 📂 data                  # Data layer
│   ├── apiservise         # API services 
│   └── dto
│   └── model
│   └── util
├── 📂 reposatory           # Business logic layer
│   ├── MutualFundRepositoryImpl          
│   └── MutualFundRepository       
├── 📂 presentation # UI layer
      
│   └── 📂 Viewmodel     
│   └── 📂 Screens
            ├── home         # Home screen (Browse/Search)
│           ├── detail   
├── 📂 di                  # Dependency injection
│   ├── Appmodule                
└── 📂 Naviagtion           # App navigation
```


### 2. **MVVM Pattern**
Used Model-View-ViewModel pattern for presentation layer:

- **View (Composables)**: UI rendering using Jetpack Compose
- **ViewModel**: Manages UI state and business logic
- **Model**: Domain models representing data

**Why MVVM?**
- Lifecycle-aware components
- Survives configuration changes
- Clear data flow: UI → ViewModel → Repository → API
- Easy state management with Compose

### 3. **Jetpack Compose**
Modern declarative UI toolkit:


### 4. **Dependency Injection with Hilt**
Used Hilt (Dagger wrapper) for dependency injection:


### 5. **Repository Pattern**
Abstracted data sources behind repository interfaces:

**Purpose:**
- Single source of truth
- Easy to swap data sources (API, database, cache)
- Centralized data operations
- Testable with mock implementations

### 6. **Kotlin Coroutines & Flow**
Asynchronous operations handled with Coroutines and Flow:

### 7. **Retrofit for Networking**
Used Retrofit for API communication:

---

## 🎨 UI/UX Decisions

### 1. **Material 3 Design**
- Modern, consistent design language
- Dynamic color theming
- Improved accessibility
- Beautiful components (Cards, Chips, TopAppBar)

### 2. **Custom Canvas Chart**
- Built custom line chart using Compose Canvas
- Smooth line visualization with gradient fill
- Grid lines for better readability
- Auto-scaling based on data range
- Interactive data points

### 3. **Search with Debouncing**
- 500ms debounce delay prevents excessive API calls
- Real-time search results
- Clear button (X icon) for quick reset
- Back button handling to clear search

### 4. **Category Filtering**
- Horizontal scrollable chips
- Instant filtering from cached data
- Visual feedback for selected category
- Auto-categorization of funds (Equity, Debt, Hybrid, etc.)

---

## 🔮 Assumptions Made

### 1. **API Assumptions**
- ✅ The API is publicly accessible and doesn't require authentication
- ✅ API response format remains consistent
- ✅ No rate limiting or pagination required
- ✅ All scheme codes are valid and active

### 2. **Data Assumptions**
- ✅ NAV data is sorted by date (newest first)
- ✅ Date format is always `dd-MM-yyyy`
- ✅ NAV values are numeric strings
- ✅ Fund names contain enough keywords for categorization

### 3. **User Behavior Assumptions**
- ✅ Users have internet connection on first launch
- ✅ Users primarily interested in top/popular funds
- ✅ Users want to compare NAV trends over time
- ✅ 5-minute cache refresh is acceptable

### 4. **"Top Funds" Logic**
**Assumption**: Top funds are from well-known fund houses:
- SBI, HDFC, ICICI, Axis, Kotak
- Aditya Birla, Reliance, Nippon, UTI

**Rationale**: These represent majority of retail investor preferences in India.

### 5. **Device Assumptions**
- ✅ Device has minimum 2GB RAM (for caching ~40k funds)
- ✅ Screen size is at least phone-sized (no tablet optimization)
- ✅ Dark/Light mode support via system settings

---





## 🔧 Configuration

### Build Configuration
- **Compile SDK**: 34
- **Min SDK**: 24
- **Target SDK**: 34
- **Java Version**: 1.8
- **Kotlin Compiler Extension**: 1.5.3



## 📊 API Integration

### Base URL
```
https://api.mfapi.in/
```

### Endpoints Used
1. **GET /mf**
   - Returns list of all mutual funds
   - Response: `List<{schemeCode, schemeName}>`

2. **GET /mf/{schemeCode}**
   - Returns fund details with NAV history
   - Response: `{meta: {scheme details}, data: [{date, nav}]}`

---

## 🚧 Future Enhancements

### Short-term
- [ ] Add pull-to-refresh on home screen
- [ ] Implement favorites/watchlist feature
- [ ] Add share functionality for fund details
- [ ] Show more fund statistics (volatility, Sharpe ratio)

### Medium-term
- [ ] Integrate Room DB for persistent caching
- [ ] Add comparison feature (compare 2+ funds)
- [ ] Implement advanced filters (returns, risk, etc.)
- [ ] Add dark/light theme toggle

### Long-term
- [ ] Portfolio tracking feature
- [ ] SIP calculator
- [ ] Push notifications for NAV updates
- [ ] Integrate with other financial APIs

---

## 📄 License

This project is created for educational/assignment purposes.

---



**Built with ❤️ using Jetpack Compose**
