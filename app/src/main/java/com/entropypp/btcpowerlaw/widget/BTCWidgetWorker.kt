package com.entropypp.btcpowerlaw.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.*
import com.entropypp.btcpowerlaw.data.BtcRepository
import com.entropypp.btcpowerlaw.data.api.RetrofitClient
import com.entropypp.btcpowerlaw.domain.model.BtcMetrics
import java.util.concurrent.TimeUnit

class BTCWidgetWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            val repository = BtcRepository(
                RetrofitClient.coinGeckoApi,
                RetrofitClient.fearAndGreedApi,
                RetrofitClient.mempoolApi
            )
            val metrics = repository.getBtcMetrics()
            
            // Save metrics to DataStore or SharedPreferences for the widget to read
            saveMetrics(metrics)
            
            DCAWidget().updateAll(context)
            BTCFearAndGreedWidget().updateAll(context)
            BTCPriceWidget().updateAll(context)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun saveMetrics(metrics: BtcMetrics) {
        val prefs = context.getSharedPreferences("btc_widget_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putFloat("currentPrice", metrics.currentPrice.toFloat())
            putFloat("ath", metrics.ath.toFloat())
            putFloat("fairPrice", metrics.fairPrice.toFloat())
            putFloat("topPrice", metrics.topZonePrice.toFloat())
            putFloat("floorPrice", metrics.floorPrice.toFloat())
            putInt("fearAndGreedIndex", metrics.fearAndGreedIndex)
            putString("fearAndGreedLabel", metrics.fearAndGreedLabel)
            putFloat("satsVb", metrics.satsPerVb.toFloat())
            putLong("blockHeight", metrics.blockHeight)
            putInt("buyRating", metrics.buyRating)
            putLong("lastUpdated", System.currentTimeMillis())
            apply()
        }
    }

    companion object {
        private const val WORK_NAME = "BtcWidgetWorker"

        fun enqueue(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<BTCWidgetWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
        
        fun enqueueImmediate(context: Context) {
             val request = OneTimeWorkRequestBuilder<BTCWidgetWorker>()
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }
}

