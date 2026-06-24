package com.entropypp.btcpowerlaw.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import com.entropypp.btcpowerlaw.R
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.TextAlign
import androidx.glance.unit.ColorProvider
import java.text.NumberFormat
import java.util.Locale

class DCAWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val prefs = context.getSharedPreferences("btc_widget_prefs", Context.MODE_PRIVATE)
        val currentPrice = prefs.getLong("currentPrice", 0L).toDouble()
        val fairPrice = prefs.getLong("fairPrice", 0L).toDouble()

        provideContent {
            DCAWidgetContent(currentPrice, fairPrice)
        }
    }

    @SuppressLint("RestrictedApi", "DefaultLocale")
    @Composable
    private fun DCAWidgetContent(currentPrice: Double, fairPrice: Double) {
        val ratioToFair = if (fairPrice > 0) currentPrice / fairPrice else 1.0
        val green = ColorProvider(R.color.green)
        val lime = ColorProvider(R.color.lime)
        val yellow = ColorProvider(R.color.yellow)
        val orange = ColorProvider(R.color.orange)
        val red = ColorProvider(R.color.red)
        val black = ColorProvider(R.color.black)

        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
            maximumFractionDigits = 0
        }

        // 1. Progress mapping logic from DCAAccumulationCard in MainActivity.kt
        val progress = when {
            ratioToFair >= 1.0 -> 0.2f * ((1.1 - ratioToFair) / 0.1).toFloat()
            ratioToFair >= 0.75 -> 0.2f + 0.2f * ((1.0 - ratioToFair) / 0.25).toFloat()
            ratioToFair >= 0.60 -> 0.4f + 0.2f * ((0.75 - ratioToFair) / 0.15).toFloat()
            ratioToFair >= 0.42 -> 0.6f + 0.2f * ((0.60 - ratioToFair) / 0.18).toFloat()
            else -> 0.8f + 0.2f * ((0.42 - ratioToFair) / 0.12).toFloat()
        }.coerceIn(0f, 1f)

        // 2. Buy label and color logic from MainActivity.kt
        val (buyLabel, buyColor) = when {
            ratioToFair <= 0.42 -> "EXTREME BUY [%.2f]".format(ratioToFair) to green
            ratioToFair <= 0.60 -> "STRONG BUY [%.2f]".format(ratioToFair) to lime
            ratioToFair <= 0.75 -> "BUY [%.2f]".format(ratioToFair) to yellow
            ratioToFair <= 1.00 -> "FAIR VALUE [%.2f]".format(ratioToFair) to orange
            else -> "OVERVALUED [%.2f]".format(ratioToFair) to red
        }

        // 3. Graphics logic for smooth scaling from BTCFearAndGreedWidget.kt
        val scaledProgress = progress * 5f
        val activeBox = scaledProgress.toInt().coerceIn(0, 4)
        val offsetInBox = if (scaledProgress >= 5f) 1f else scaledProgress % 1.0f

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(black)
                .cornerRadius(10.dp)
                .padding(10.dp)
                .clickable(actionRunCallback<RefreshAction>()),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Header Row: Bitcoin Price and Rating Label
            Row(modifier = GlanceModifier.fillMaxWidth().height(26.dp)){
                Text(
                    text = currencyFormatter.format(currentPrice),
                    modifier = GlanceModifier.defaultWeight(),
                    style = TextStyle(
                        color = orange,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start
                    )
                )
                Text(
                    text = buyLabel,
                    modifier = GlanceModifier.defaultWeight(),
                    style = TextStyle(
                        color = buyColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Indicator Arrow (using alignment logic from BTCFearAndGreedWidget for "smooth" scaling)
            Row(modifier = GlanceModifier.fillMaxWidth().height(16.dp)) {
                for (i in 0 until 5) {
                    Box(
                        modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                        contentAlignment = if (i == activeBox) {
                            when {
                                offsetInBox < 0.25f -> Alignment.CenterStart
                                offsetInBox > 0.75f -> Alignment.CenterEnd
                                else -> Alignment.Center
                            }
                        } else Alignment.Center
                    ) {
                        if (i == activeBox) {
                            Image(
                                provider = ImageProvider(R.drawable.ic_down_arrow),
                                contentDescription = "Indicator",
                                modifier = GlanceModifier.size(16.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }

            // The Color Bar (5 segment Row)
            data class Band(
                val color: ColorProvider,
                val label: String,
            )
            Row(modifier = GlanceModifier.fillMaxWidth().height(20.dp).cornerRadius(4.dp)) {
                val formattedRatio = "%.2f".format(ratioToFair)
                val bands = listOf(
                    Band(red, if (ratioToFair > 1.0) formattedRatio else "> 1.0"),
                    Band(orange, if (ratioToFair <= 1.0 && ratioToFair > 0.75) formattedRatio else "0.75-1.0"),
                    Band(yellow, if (ratioToFair <= 0.75 && ratioToFair > 0.60) formattedRatio else "0.6-0.75"),
                    Band(lime, if (ratioToFair <= 0.60 && ratioToFair > 0.42) formattedRatio else "0.42-0.6"),
                    Band(green, if (ratioToFair <= 0.42) formattedRatio else "< 0.42")
                )
                bands.forEach { band ->
                    Box(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .fillMaxHeight()
                            .background(band.color),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = band.label,
                            style = TextStyle(
                                color = black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
