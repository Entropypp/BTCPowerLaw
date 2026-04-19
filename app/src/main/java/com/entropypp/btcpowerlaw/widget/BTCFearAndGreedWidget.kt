package com.entropypp.btcpowerlaw.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
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
import com.entropypp.btcpowerlaw.R
import java.text.NumberFormat
import java.util.Locale

class BTCFearAndGreedWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val prefs = context.getSharedPreferences("btc_widget_prefs", Context.MODE_PRIVATE)
        val fearAndGreedIndex = prefs.getInt("fearAndGreedIndex", 0)
        val currentPrice = prefs.getFloat("currentPrice", 0f).toDouble()

        provideContent {
            FearAndGreedContent(fearAndGreedIndex, currentPrice)
        }
    }
    val nunberFormatter = NumberFormat.getIntegerInstance()

    @SuppressLint("RestrictedApi", "DefaultLocale")
    @Composable
    private fun FearAndGreedContent(index: Int, currentPrice: Double) {
        val green = ColorProvider(R.color.green)
        val lime = ColorProvider(R.color.lime)
        val yellow = ColorProvider(R.color.yellow)
        val orange = ColorProvider(R.color.orange)
        val red = ColorProvider(R.color.red)
        val black = ColorProvider(R.color.black)
        val white = ColorProvider(R.color.white)
        val grey = ColorProvider(R.color.grey)
        val purple = ColorProvider(R.color.purple)


        val (fngLabel, fngColor) = when {
            index <= 24 -> "EXTREME FEAR [$index]" to red
            index <= 46 -> "FEAR [$index]" to orange
            index <= 49 -> "NEUTRAL [$index]" to yellow
            index <= 74 -> "GREED [$index]" to lime
            else -> "EXTREME GREED [$index]" to green
        }

        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
            maximumFractionDigits = 0
        }

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(black)
                .cornerRadius(10.dp)
                .padding(10.dp),
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
                    text = fngLabel,
                    modifier = GlanceModifier.defaultWeight(),
                    style = TextStyle(
                        color = fngColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                )
            }

            // Gauge Section
            //0–24: Extreme fear
            //25–44: Fear
            //45–55: Neutral
            //56–75: Greed
            //76–100: Extreme greed
            // 1. Indicator Arrow (Linear Mapping 0-100 using sentiment ranges)
            Row(modifier = GlanceModifier.fillMaxWidth().height(16.dp)) {
                val activeBox = when {
                    index <= 24 -> 0
                    index <= 46 -> 1
                    index <= 49 -> 2
                    index <= 74 -> 3
                    else -> 4
                }
                val offsetInBox = when (activeBox) {
                    0 -> index.toFloat() / 24f
                    1 -> (index - 25).toFloat() / (46f - 25f)
                    2 -> (index - 47).toFloat() / (49f - 47f)
                    3 -> (index - 50).toFloat() / (74f - 50f)
                    else -> (index - 75).toFloat() / (100f - 75f)
                }.coerceIn(0f, 1f)
                
                Row(modifier = GlanceModifier.fillMaxWidth()) {
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
            }
            // 2. The Color Bar (5 segment Row)
            data class Band(
                val color: ColorProvider,
                val label: String,
            )
            Row(modifier = GlanceModifier.fillMaxWidth().height(20.dp).cornerRadius(4.dp)) {
                val bands = listOf(
                    Band(red, if (index in 0..24) "$index" else "0-24"),
                    Band(orange, if (index in 25..46) "$index" else "25-46"),
                    Band(yellow, if (index in 47..49) "$index" else "47-49"),
                    Band(lime, if (index in 50..74) "$index" else "50-74"),
                    Band(green, if (index in 75..100) "$index" else "75-100")
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
                            modifier = GlanceModifier.padding(top = 1.dp),
                            style = TextStyle(
                                color = black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
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
