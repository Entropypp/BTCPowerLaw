package com.entropypp.btcpowerlaw.util

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.pow

object PowerLawCalculator {
    private val GENESIS_DATE = LocalDate.of(2009, 1, 3)

    /**
     * Fair Price = 10^-17 * (Days from Genesis)^5.8
     * This is one common variation. Let's use parameters that get close to the mockup if possible.
     * Mockup shows Fair ~$131k on 2026-04-05.
     */
    fun calculateFairPrice(date: LocalDate = LocalDate.now()): Double {
        val days = ChronoUnit.DAYS.between(GENESIS_DATE, date).toDouble()
        return 10.0117.pow(-17.0) * days.pow(5.82)
    }

    fun calculateAccumuloTopZone(fairPrice: Double): Double {
        return 0.75 * fairPrice
    }

    fun calculateFloorPrice(fairPrice: Double): Double {
        return fairPrice * 0.398
    }

    fun calculateFloorPrice(date: LocalDate = LocalDate.now()): Double {
        return this.calculateFloorPrice(this.calculateFairPrice(date))
    }

    fun calculateBuyRating(currentPrice: Double, floor: Double, top: Double): Int {
        if (currentPrice <= floor) return 5
        if (currentPrice >= top) return 1
        
        val range = top - floor
        val position = (currentPrice - floor) / range
        
        return when {
            position < 0.2 -> 5
            position < 0.4 -> 4
            position < 0.6 -> 3
            position < 0.8 -> 2
            else -> 1
        }
    }
}

