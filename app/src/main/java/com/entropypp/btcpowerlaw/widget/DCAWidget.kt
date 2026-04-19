package com.entropypp.btcpowerlaw.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import com.entropypp.btcpowerlaw.R
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
import com.entropypp.btcpowerlaw.ui.theme.BtcOrange
import java.text.NumberFormat
import java.util.Locale


class DCAWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val prefs = context.getSharedPreferences("btc_widget_prefs", Context.MODE_PRIVATE)
        val currentPrice = prefs.getFloat("currentPrice", 0f).toDouble()
        val fairPrice = prefs.getFloat("fairPrice", 0f).toDouble()

        provideContent {
            DCAWidgetContent(currentPrice, fairPrice)
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun DCAWidgetContent(currentPrice: Double, fairPrice: Double) {
        val ratioToFair = if (fairPrice > 0) currentPrice / fairPrice else 1.0
        val green = ColorProvider(R.color.green)
        val lime = ColorProvider(R.color.lime)
        val yellow = ColorProvider(R.color.yellow)
        val orange = ColorProvider(R.color.orange)
        val red = ColorProvider(R.color.red)
        val black = ColorProvider(R.color.black)
        val white = ColorProvider(R.color.white)
        val grey = ColorProvider(R.color.grey)
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
            maximumFractionDigits = 0
        }
            val (buyLabel, buyColor) = when {
            ratioToFair <= 0.42 -> "EXTREME BUY [5X]" to green
            ratioToFair <= 0.60 -> "STRONG BUY [4X]" to lime
            ratioToFair <= 0.75 -> "BUY [3X]" to yellow
            ratioToFair <= 1.00 -> "FAIR VALUE [2X]" to orange
            ratioToFair <= 1.50 -> "OVERVALUED (1X]" to red
            else -> "OVERBOUGHT [1X]" to red
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
            // Header Row: Title and Rating Label
            Row(modifier = GlanceModifier.fillMaxWidth().height(26.dp)){
                Text(
                    text = currencyFormatter.format(currentPrice),
                    modifier = GlanceModifier.wrapContentWidth(),
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

            //(modifier = GlanceModifier.height(8.dp))

            // Gauge Section

            // 1. Indicator Arrow (using Vector Drawable for full fill)
            Row(modifier = GlanceModifier.fillMaxWidth().height(16.dp)) {
                val activeIndex = when {
                    ratioToFair <= 0.42 -> 0
                    ratioToFair <= 0.60 -> 1
                    ratioToFair <= 0.75 -> 2
                    ratioToFair <= 1.00 -> 3
                    else -> 4
                }
                for (i in 0 until 5) {
                    Box(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (i == activeIndex) {
                            Image(
                                provider = ImageProvider(R.drawable.ic_down_arrow),
                                contentDescription = "Indicator",
                                modifier = GlanceModifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
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
                    Band(green,"5X"),
                    Band(lime,"4X"),
                    Band(yellow,"3X"),
                    Band(orange,"2X"),
                    Band(red,"1X")
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
            //  Ticks and Labels (Matching centers of the 5 segments)
            /*Row(modifier = GlanceModifier.fillMaxWidth().height(24.dp)) {
                val ticks = listOf("5X", "4X", "3X", "2X", "1X")
                ticks.forEach { tick ->
                    Box(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tick,
                            style = TextStyle(
                                color = grey,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }*/
        }
    }
}
