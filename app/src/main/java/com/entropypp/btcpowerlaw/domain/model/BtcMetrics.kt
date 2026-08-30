package com.entropypp.btcpowerlaw.domain.model

data class BtcMetrics(
    val currentPrice: Double,
    val ath: Double,
    val fairPrice: Double,
    val drawdown: Double,
    val topZonePrice: Double,
    val floorPrice: Double,
    val fearAndGreedIndex: Int,
    val fearAndGreedLabel: String,
    val satsPerVb: Double,
    val blockHeight: Long,
    val buyRating: Int // 1 to 5 stars
)

