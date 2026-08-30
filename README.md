# Bitcoin Power Law

An Android application that tracks Bitcoin's price relative to the **Power Law Corridor**, providing data-driven insights into market cycles, valuation bands, and risk levels.

## Features

- **Power Law Analysis**: Real-time calculation of Bitcoin's "Fair Value" based on the power law model ($10^{-17} \times \text{days}^{5.8}$).
- **Valuation Bands**: View precise price levels for the **Floor (0.398x)** and **Ceiling (2.512x)** zones.
- **Percentage Drawdown**: A color-coded metric showing the current price decline from the All-Time High (ATH).
  - 🟢 0-5%: Green
  - 🟡 5-20%: Lime/Yellow
  - 🟠 20-30%: Orange
  - 🔴 >30%: Red
- **Fear & Greed Index**: Integrated sentiment analysis to gauge market extremes.
- **Buy Rating**: A 5-star rating system based on the price's position within the Power Law accumulation zones.
- **Interactive Time Machine**: Select any historical or future date to see model projections and historical metrics.
- **Home Screen Widget**: A sleek, dark-themed Android widget powered by **Jetpack Glance** for quick price and sentiment updates.

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Asynchronous Flow**: Kotlin Coroutines & Flow
- **Networking**: Retrofit & OkHttp
- **Local Storage**: Room & DataStore (Preferences)
- **Background Tasks**: WorkManager (for widget updates)
- **Widgets**: Jetpack Glance
- **Design**: Custom "Rajdhani" typography with a high-contrast dark theme.

## Data Sources

The app aggregates data from authoritative Bitcoin APIs:
- **Mempool.space**: For real-time price, block height, and historical data.
- **CoinGecko**: For market data, including All-Time High (ATH) tracking.
- **Alternative.me**: For the Fear and Greed Index.

## Getting Started

1. Clone the repository.
2. Open the project in **Android Studio (Ladybug or newer)**.
3. Sync Gradle and run the `:app` module on an emulator or physical device (Min SDK: 31).

## Disclaimer

This application is for informational purposes only. The Bitcoin Power Law is a mathematical model based on historical data and does not constitute financial advice. Past performance is not indicative of future results.
