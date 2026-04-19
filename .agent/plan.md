# Project Plan

Create an Android widget called "BTC Power Law Widger" that displays Bitcoin price and metrics. The widget should show:
- Fair price, Top price, and Floor price (calculated using Bitcoin Power Law).
- Bitcoin Price, ATH, Fear and Greed index, Sats/vB, and Block height (fetched from APIs).
- Buy rating (stars) calculated based on Power Law and current price.
The UI should match the provided image: a dark background with vibrant green and orange text, using a specific blocky/digital font style.

## Project Brief

# Project Brief: BTC Power Law Widger

An Android application and home screen widget designed to provide Bitcoin investors with key valuation metrics based on the Bitcoin Power Law model, alongside real-time market data.

## Features

- **Power Law Valuation Metrics**: Calculates and displays "Fair Price," "Top Price," and "Floor Price" using the Bitcoin Power Law model to help users identify market cycles.
- **Real-time Market Data Integration**: Fetches live Bitcoin price, All-Time High (ATH), Fear & Greed Index, current Sats/vB (network fees), and latest Block height from external APIs.
- **Dynamic Buy Rating**: Automatically generates a star-based "Buy Rating" by comparing the current Bitcoin price against Power Law support and resistance levels.
- **Retro-Styled Glance Widget**: A highly legible Home Screen widget featuring a dark, digital aesthetic with vibrant green and orange indicators for instant market updates.

## High-Level Technical Stack

- **Kotlin**: Primary programming language for modern, concise Android development.
- **Jetpack Compose**: For building the application's UI with a reactive, declarative approach.
- **Jetpack Glance**: Specialized framework for building the Home Screen widget using Compose-like syntax.
- **Retrofit & Moshi**: For handling API requests and parsing JSON data efficiently.
- **Kotlin Coroutines & Flow**: For managing asynchronous tasks and real-time data streams.
- **KSP (Kotlin Symbol Processing)**: For high-performance code generation.

## UI Design Image
C:/Users/User/AndroidStudioProjects/BTCPowerLaw/input_images/image_0.png

## Implementation Steps
**Total Duration:** 51m 36s

### Task_1_DataLayer: Setup dependencies and implement the Data Layer. Add Jetpack Glance to the project. Create Retrofit services to fetch Bitcoin price, ATH, Fear and Greed Index, and network metrics (Sats/vB, Block height). Implement the Bitcoin Power Law calculation logic and Buy Rating algorithm.
- **Status:** COMPLETED
- **Updates:** - Fixed CoinGecko API parsing error by switching from 'simple/price' to '/coins/markets' endpoint, which reliably provides ATH data.
- **Acceptance Criteria:**
  - Jetpack Glance dependency added to build.gradle.kts
  - API services for Bitcoin data are implemented and functional
  - Power Law calculations (Fair, Top, Floor prices) are accurate
  - Buy Rating logic is implemented based on Power Law levels
- **Duration:** 15m 55s

### Task_2_AppUI: Implement the Design System and Main Activity. Define a Material 3 theme with the specified vibrant green and orange colors. Set up the 'blocky/digital' typography using a suitable font. Build the MainActivity using Jetpack Compose to display all calculated and fetched metrics for testing.
- **Status:** COMPLETED
- **Updates:** - Material 3 theme implemented with vibrant green (#B6D94A) and BTC orange (#F7931A) on a black background.
- **Acceptance Criteria:**
  - Material 3 theme with green/orange color scheme implemented
  - Digital/Monospace font applied to UI text
  - MainActivity displays Price, Fair, Top, Floor, ATH, Fear/Greed, and Buy Rating
  - UI follows Edge-to-Edge display guidelines
- **Duration:** 23m 22s

### Task_3_GlanceWidget: Implement the Home Screen Widget using Jetpack Glance. Design the widget layout to strictly match the provided reference image (input_images/image_0.png), featuring the large central price and color-coded metrics.
- **Status:** COMPLETED
- **Updates:** - Created BtcWidget using Jetpack Glance with a layout matching the provided reference image (C:/Users/User/AndroidStudioProjects/BTCPowerLawWidger/input_images/image_0.png).
- **Acceptance Criteria:**
  - Glance widget is created and can be added to the home screen
  - Widget UI matches the design in C:/Users/User/AndroidStudioProjects/BTCPowerLawWidger/input_images/image_0.png
  - Widget displays real-time (or cached) Bitcoin data correctly
- **Duration:** 8m 15s

### Task_4_Finalization: Configure background updates and finalize the app. Implement periodic data refreshing using WorkManager or Glance's update mechanism. Create an adaptive app icon matching the Bitcoin theme. Perform final verification.
- **Status:** COMPLETED
- **Updates:** - Verified the app and widget are stable and functionally complete after the data layer fix.
- Re-verified UI consistency: Material 3 design, vibrant green/orange colors, and digital/monospace font (Share Tech Mono) are correctly implemented.
- Verified all metrics (Fair, Top, Floor, Price, ATH, Fear/Greed, Buy Rating, Sats/vB, Block height) are displayed accurately in both the app and the Glance widget.
- Confirmed full Edge-to-Edge display and adaptive app icon implementation.
- Verified periodic background updates via WorkManager (every 30 mins).
- The project is complete and stable.
- **Acceptance Criteria:**
  - Widget updates periodically in the background
  - Adaptive app icon is implemented
  - Full project builds successfully
  - Run and Verify: App and widget are stable, no crashes, and UI matches requirements and design image. Confirm alignment with user requirements and report any issues.
- **Duration:** 4m 4s

