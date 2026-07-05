package com.entropypp.btcpowerlaw.data

import android.content.Context
import com.entropypp.btcpowerlaw.data.api.CoinGeckoApi
import com.entropypp.btcpowerlaw.data.api.FearAndGreedApi
import com.entropypp.btcpowerlaw.data.api.MempoolApi
import com.entropypp.btcpowerlaw.data.model.MempoolHistoricalPriceResponse
import com.entropypp.btcpowerlaw.data.model.MempoolPrices
import com.entropypp.btcpowerlaw.domain.model.BtcMetrics
import com.entropypp.btcpowerlaw.util.PowerLawCalculator
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class BtcRepository(
    private val coinGeckoApi: CoinGeckoApi,
    private val fearAndGreedApi: FearAndGreedApi,
    private val mempoolApi: MempoolApi,
    private val context: Context? = null
) {
    companion object {
        private var cachedAth: Double = 0.0
    }

    private fun getPersistedAth(): Double {
        if (cachedAth > 0) return cachedAth
        context?.let {
            val prefs = it.getSharedPreferences("btc_widget_prefs", Context.MODE_PRIVATE)
            cachedAth = prefs.getLong("ath", 0).toDouble()
        }
        return cachedAth
    }

    private fun persistAth(ath: Double) {
        if (ath <= 0) return
        cachedAth = ath
        context?.let {
            val prefs = it.getSharedPreferences("btc_widget_prefs", Context.MODE_PRIVATE)
            prefs.edit().putLong("ath", ath.toLong()).apply()
        }
    }

    suspend fun getBtcMetrics(date: LocalDate = LocalDate.now()): BtcMetrics = coroutineScope {
        val today = LocalDate.now()
        
        val fngDeferred = async { 
            try { 
                if (date.isBefore(today)) {
                    fearAndGreedApi.getFearAndGreed(limit = 0) 
                } else {
                    fearAndGreedApi.getFearAndGreed(limit = 1)
                }
            } catch (e: Exception) { null }
        }

        val marketDataDeferred = async {
            try { 
                val res = coinGeckoApi.getMarkets()
                val ath = res.firstOrNull()?.ath ?: 0.0
                if (ath > 0) {
                    persistAth(ath)
                }
                res
            } catch (e: Exception) { 
                null 
            }
        }
        
        val feesDeferred = async { 
            try { mempoolApi.getFees() } catch (e: Exception) { null }
        }
        
        val blockHeightDeferred = async { 
            try { mempoolApi.getBlockHeight() } catch (e: Exception) { 0L }
        }
        
        val priceDeferred = if (date.isBefore(today)) {
            val timestamp = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC)
            async<Any?> { 
                try { mempoolApi.getHistoricalPrice(timestamp) } catch (e: Exception) { null }
            }
        } else {
            async<Any?> { 
                try { mempoolApi.getCurrentPrice() } catch (e: Exception) { null }
            }
        }

        val feesRes = feesDeferred.await()
        val blockHeight = blockHeightDeferred.await()
        val priceRes = priceDeferred.await()

        val fngResRaw = fngDeferred.await()
        val marketDataRes = marketDataDeferred.await()?.firstOrNull()
        
        val ath = if (marketDataRes?.ath != null && marketDataRes.ath > 0) marketDataRes.ath else getPersistedAth()

        val displayPrice = when (priceRes) {
            is MempoolPrices -> priceRes.usd
            is MempoolHistoricalPriceResponse -> priceRes.prices.firstOrNull()?.usd ?: 0.0
            else -> 0.0
        }

        val drawdown = if (ath > 0) {
            ((ath - displayPrice) / ath) * 100.0
        } else {
            0.0
        }

        val fngRes = if (date.isBefore(today)) {
            fngResRaw?.data?.find { item ->
                try {
                    val itemDate = Instant.ofEpochSecond(item.timestamp?.toLong() ?: 0)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate()
                    itemDate == date
                } catch (e: Exception) {
                    false
                }
            }
        } else {
            fngResRaw?.data?.firstOrNull()
        }

        val fairPrice = PowerLawCalculator.calculateFairPrice(date)
        val topZonePrice = PowerLawCalculator.calculateAccumuloTopZone(fairPrice)
        val floorPrice = PowerLawCalculator.calculateFloorPrice(date)

        // For future dates, we use today's bands to calculate the buy rating since we are using today's price
        val ratingDate = if (date.isAfter(today)) today else date
        val fairForRating = PowerLawCalculator.calculateFairPrice(ratingDate)
        val topForRating = PowerLawCalculator.calculateAccumuloTopZone(fairForRating)
        val floorForRating = PowerLawCalculator.calculateFloorPrice(ratingDate)

        BtcMetrics(
            currentPrice = displayPrice,
            ath = ath,
            fairPrice = fairPrice,
            drawdown = drawdown,
            topZonePrice = topZonePrice,
            floorPrice = floorPrice,
            fearAndGreedIndex = fngRes?.value?.toIntOrNull() ?: 50,
            fearAndGreedLabel = fngRes?.valueClassification ?: "Unknown",
            satsPerVb = feesRes?.halfHourFee?.toDouble() ?: 0.0,
            blockHeight = blockHeight,
            buyRating = PowerLawCalculator.calculateBuyRating(displayPrice, floorForRating, topForRating)
        )
    }
}
